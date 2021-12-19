INSERT
  INTO tmp_item_file_balance
  (
    ifb_file_id,
    ifb_item_id,
    ifb_fine_item_id,
    ifb_level,
    ifb_number,
    ifb_parent_number,
    ifb_subtotal_flag
  )
WITH
     w_raw AS
     (
         SELECT
                file_id,
                rn,
                item_id,
                CASE
                     WHEN rn1 = rn2 THEN
                         NULL
                     WHEN rn2 = 1 THEN
                         parent_n1
                     ELSE
                         parent_n2
                END AS parent_rn,
                rn_reverse,
                major_level + minor_level AS statement_level
           FROM (SELECT
                        file_id,
                        rn,
                        item_id,
                        FIRST_VALUE(rn) OVER (win_major)  AS parent_n1,
                        FIRST_VALUE(rn) OVER (win_minor)  AS parent_n2,
                        ROW_NUMBER()    OVER (win_major)  AS rn1,
                        ROW_NUMBER()    OVER (win_minor)  AS rn2,
                        ROW_NUMBER()    OVER (win_common) AS rn_reverse,
                        CASE major_level
                             WHEN 0 THEN
                                 0
                             ELSE
                                 1
                        END AS major_level,
                        CASE minor_level
                             WHEN 0 THEN
                                 0
                             ELSE
                                 1
                        END AS minor_level
                   FROM (SELECT
                                file.file_id,
                                statement_number AS rn,
                                item.item_id,
                                CAST(SUBSTR(statement_level, 1, 2) AS INTEGER) AS major_level,
                                CAST(SUBSTR(statement_level, 3, 2) AS INTEGER) AS minor_level,
                                statement_level
                           FROM src.raw_statement stmt
                                JOIN
                                tbl_file file
                                    ON file.file_name = stmt.file_name || '.xlsx'
                                JOIN
                                tbl_item item
                                    ON item.item_name = stmt.statement
                          WHERE stmt.statement_number != 0
                            AND stmt.sheet = 'BALANCE'
                            AND EXISTS(SELECT
                                              NULL
                                         FROM tbl_emitter emit
                                        WHERE emit.emitter_id = file.file_emitter_id
                                          AND emit.emitter_name = stmt.emitent))
                 WINDOW win_major AS (PARTITION BY file_id,
                                                   major_level
                                          ORDER BY rn),
                        win_minor AS (PARTITION BY file_id,
                                                   major_level,
                                                   minor_level
                                          ORDER BY rn),
                        win_common AS (PARTITION BY file_id
                                           ORDER BY rn DESC))
     ),
     w_pre AS
     (
         SELECT
                file_id,
                rn,
                item_id,
                parent_rn,
                statement_level,
                cl_rn,
                dense_cl_rn,
                CASE
                     WHEN pre_update_flag = 'UPDATE' AND pre_update_rn = 2 THEN
                         rn
                END AS update_rn,
                CASE
                     WHEN pre_update_flag = 'UPDATE' AND pre_update_rn >= 2 THEN
                         'UPDATE'
                END AS update_flag
           FROM (SELECT
                        file_id,
                        rn,
                        item_id,
                        parent_rn,
                        statement_level,
                        cl_rn,
                        dense_cl_rn,
                        pre_update_flag,
                        COUNT(pre_update_flag) OVER (win) AS pre_update_rn
                   FROM (SELECT
                                file_id,
                                rn,
                                item_id,
                                parent_rn,
                                statement_level,
                                cl_rn,
                                dense_cl_rn,
                                CASE parent_rn
                                     WHEN dense_cl_rn THEN
                                         'UPDATE'
                                END AS pre_update_flag
                           FROM (SELECT
                                        file_id,
                                        rn,
                                        item_id,
                                        parent_rn,
                                        statement_level,
                                        cl_rn,
                                        MAX(cl_rn) OVER(win) AS dense_cl_rn
                                   FROM (SELECT
                                                file_id,
                                                rn,
                                                item_id,
                                                parent_rn,
                                                statement_level,
                                                CASE rn_reverse
                                                     WHEN 1 THEN
                                                         IFNULL(parent_rn, LAG(parent_rn) OVER (win))
                                                END AS cl_rn
                                           FROM w_raw
                                         WINDOW win AS (PARTITION BY file_id
                                                            ORDER BY rn))
                                 WINDOW win AS (PARTITION BY file_id)))
                 WINDOW win AS (PARTITION BY file_id
                                    ORDER BY rn))
     ),
     w_a AS
     (
         SELECT
                file_id,
                rn,
                parent_rn,
                item_id,
                cl_rn,
                dense_cl_rn,
                CAST(update_rn + prev_update_rn AS REAL)/2 AS new_rn,
                update_flag
           FROM (SELECT
                        file_id,
                        rn,
                        parent_rn,
                        item_id,
                        cl_rn,
                        dense_cl_rn,
                        MAX(update_rn)      OVER(win) AS update_rn,
                        MAX(prev_update_rn) OVER(win) AS prev_update_rn,
                        update_flag
                   FROM (SELECT
                               file_id,
                               rn,
                               parent_rn,
                               item_id,
                               cl_rn,
                               dense_cl_rn,
                               update_rn,
                               CASE
                                    WHEN update_rn IS NOT NULL THEN
                                        LAG(rn) OVER (win)
                               END AS prev_update_rn,
                               update_flag
                           FROM w_pre
                         WINDOW win AS (PARTITION BY file_id
                                            ORDER BY rn))
                  WINDOW win AS (PARTITION BY file_id))
     ),
     w_ready AS
     (
         SELECT
                file_id,
                rn,
                parent_rn,
                item_id,
                CASE
                     WHEN parent_rn IS NOT NULL
                      AND subtotal_rn = 1 THEN
                         'SUBTOTAL'
                     ELSE
                         'NOT_SUBTOTAL'
                END AS subtotal_flag
           FROM (SELECT
                        file_id,
                        rn,
                        parent_rn,
                        item_id,
                        ROW_NUMBER() OVER (win) AS subtotal_rn
                   FROM (SELECT
                                file_id,
                                rn,
                                CASE update_flag
                                     WHEN 'UPDATE' THEN
                                         new_rn
                                     ELSE
                                         IFNULL(parent_rn, cl_rn)
                                END AS parent_rn,
                                item_id
                           FROM w_a
                          UNION ALL
                         SELECT
                                t.file_id,
                                t.rn,
                                t.parent_rn,
                                item.item_id
                           FROM (SELECT
                                        file_id,
                                        MAX(new_rn)      AS rn,
                                        MAX(dense_cl_rn) AS parent_rn
                                   FROM w_a
                                  GROUP BY
                                           file_id) t
                                CROSS JOIN
                                tbl_item item
                          WHERE item.item_name = 'Обязательства')
                 WINDOW win AS (PARTITION BY file_id,
                                             parent_rn
                                    ORDER BY rn DESC))
     ),
     works_for_root(file_id, rn, parent_rn, item_id, subtotal_flag) AS
     (
         SELECT
                file_id,
                rn,
                parent_rn,
                item_id,
                subtotal_flag
           FROM w_ready
          UNION ALL
         SELECT
                ready.file_id,
                ready.rn,
                ready.parent_rn,
                ready.item_id,
                ready.subtotal_flag
           FROM works_for_root root
                JOIN
                w_ready ready
                    ON ready.file_id = root.file_id
                   AND IFNULL(ready.parent_rn, 0) = IFNULL(root.rn, 0)
     )
SELECT
       t.file_id              AS ifb_file_id,
       t.item_id              AS ifb_item_id,
       fine_item.fine_item_id AS ifb_fine_item_id,
       t.statement_level      AS ifb_level,
       t.rn                   AS ifb_number,
       t.parent_rn            AS ifb_parent_number,
       t.subtotal_flag        AS ifb_subtotal_flag
  FROM (SELECT
               file_id,
               rn,
               MAX(parent_rn)     AS parent_rn,
               MAX(item_id)       AS item_id,
               COUNT(*)           AS statement_level,
               MAX(subtotal_flag) AS subtotal_flag
          FROM works_for_root
         GROUP BY
                  file_id,
                  rn) t
       CROSS JOIN
       tbl_fine_item fine_item
 WHERE fine_item.fine_item_name = 'TECH$BLANC'
