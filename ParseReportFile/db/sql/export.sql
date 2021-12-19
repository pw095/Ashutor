WITH
     w_pre AS
     (
         SELECT
                ifb_id,
                ifb_file_id,
                ifb_parent_id,
                ifb_item_id,
                ifb_number,
                CASE
                     WHEN rn1 = 2
                      AND rn_reverse = 1 THEN
                         0
                     ELSE
                         rn1
                END AS rn1,
                CASE rn1
                     WHEN 1 THEN
                          CASE rn_reverse
                               WHEN 1 THEN
                                   0
                               ELSE
                                   ifb_level_diff
                          END
                     WHEN 2 THEN
                          CASE rn_reverse
                               WHEN 1 THEN
                                   0
                               WHEN 2 THEN
                                   0
                               ELSE
                                   ifb_level_diff
                          END
                END AS rn2
           FROM (SELECT
                        ifb_id,
                        ifb_file_id,
                        ifb_parent_id,
                        ifb_item_id,
                        ifb_number,
                        rn1,
                        ROW_NUMBER()        OVER (PARTITION BY rn1, ifb_file_id ORDER BY ifb_number DESC) AS rn_reverse,
                        SUM(ifb_level_diff) OVER (PARTITION BY rn1, ifb_file_id ORDER BY ifb_number) - 1  AS ifb_level_diff
                   FROM (SELECT
                                ifb_id,
                                ifb_file_id,
                                ifb_parent_id,
                                ifb_item_id,
                                ifb_number,
                                MAX(lead_ifb_level - ifb_level, 0)                             AS ifb_level_diff,
                                COUNT(rn1) OVER (PARTITION BY ifb_file_id ORDER BY ifb_number) AS rn1
                           FROM (SELECT
                                        ifb_id,
                                        ifb_file_id,
                                        ifb_parent_id,
                                        ifb_item_id,
                                        ifb_number,
                                        ifb_level,
                                        lead_ifb_level,
                                        CASE
                                             WHEN ifb_level = 1
                                              AND lead_ifb_level = 2 THEN
                                                 1
                                        END AS rn1
                                   FROM (SELECT
                                                ifb.ifb_id,
                                                ifb.ifb_file_id,
                                                ifb.ifb_parent_id,
                                                ifb.ifb_item_id,
                                                ifb.ifb_number,
                                                ifb.ifb_level,
                                                LEAD(ifb.ifb_level) OVER (PARTITION BY ifb.ifb_file_id ORDER BY ifb.ifb_number) AS lead_ifb_level
                                           FROM tbl_item_file_balance ifb
                                          WHERE NOT EXISTS(SELECT
                                                                  NULL
                                                             FROM tbl_item item
                                                            WHERE item.item_id = ifb.ifb_item_id
                                                              AND item.item_name = 'Обязательства')))))
     ),
     w_match AS
     (
       SELECT
              master_ifb_id,
              COUNT(*)  AS match_cnt
         FROM tbl_item_file_balance_match
        GROUP BY
                 master_ifb_id
     ),
     w_emit_file AS
     (
       SELECT
              file_emitter_id,
              COUNT(*)        AS file_cnt
         FROM tbl_file
        GROUP BY
                 file_emitter_id
     )
SELECT
       emitent,
       sheet,
       statement_number,
       file_name,
       file_cnt,
       match_cnt,
       statement,
       statement_level
  FROM (SELECT
               emit.emitter_name                                            AS emitent,
               'BALANCE'                                                    AS sheet,
               ifb.ifb_number                                               AS statement_number,
               file.file_name,
               emit_file.file_cnt,
               1 + IFNULL(match.match_cnt, 0)                               AS match_cnt,
               item.item_name                                               AS statement,
               '0' || CAST(ifb.rn1 AS TEXT) || '0' || CAST(ifb.rn2 AS TEXT) AS statement_level
          FROM w_pre ifb
               LEFT JOIN
               tbl_item_file_balance ifb_parent
                   ON ifb_parent.ifb_id = ifb.ifb_parent_id
               JOIN
               tbl_file file
                   ON file.file_id = ifb.ifb_file_id
               JOIN
               tbl_emitter emit
                   ON emit.emitter_id = file.file_emitter_id
               JOIN
               tbl_item item
                   ON item.item_id = ifb.ifb_item_id
               LEFT JOIN
               w_match match
                   ON match.master_ifb_id = ifb.ifb_id
               JOIN
               w_emit_file emit_file
                   ON emit_file.file_emitter_id = file.file_emitter_id
         WHERE NOT EXISTS(SELECT
                                 NULL
                            FROM tbl_item_file_balance_match mtch
                           WHERE mtch.depend_ifb_id = ifb.ifb_id))
 WHERE file_cnt != match_cnt
