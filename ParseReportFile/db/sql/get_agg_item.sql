WITH RECURSIVE
    w_pre AS
    (
        SELECT
               file.file_emitter_id,
               file.file_date,
               ifb.ifb_subtotal_flag,
               ifb.ifb_id,
               ifb.ifb_parent_id,
               fine_item.fine_item_code,
               ifb.ifb_index,
               pure_item.pure_item_name,
               NULL                     AS hier_path
          FROM tbl_item_file_balance ifb
               JOIN
               tbl_item item
                   ON item.item_id = ifb.ifb_item_id
               JOIN
               tbl_pure_item pure_item
                   ON pure_item.pure_item_id = item.pure_item_id
               JOIN
               tbl_file file
                   ON file.file_id = ifb.ifb_file_id
               JOIN
               tbl_fine_item fine_item
                   ON fine_item.fine_item_id = ifb.ifb_fine_item_id
                  --AND fine_item.fine_item_code != 'TECH$BLANC'
    ),
    w_hier(file_emitter_id, file_date, ifb_subtotal_flag, ifb_id, ifb_parent_id, fine_item_code, ifb_index, level, hier_pure_item_path) AS
    (
        SELECT
               file_emitter_id,
               file_date,
               ifb_subtotal_flag,
               ifb_id,
               ifb_parent_id,
               fine_item_code,
               ifb_index,
               0                AS level,
               --fine_item_name   AS hier_fine_item_path,
               pure_item_name   AS hier_pure_item_path
          FROM w_pre
         WHERE ifb_parent_id IS NULL
         UNION ALL
        SELECT
               pre.file_emitter_id,
               pre.file_date,
               pre.ifb_subtotal_flag,
               pre.ifb_id,
               pre.ifb_parent_id,
               pre.fine_item_code,
               pre.ifb_index,
               hier.level + 1                                          AS level,
               hier.hier_pure_item_path || ' > ' || pre.pure_item_name AS hier_pure_item_path
          FROM w_pre pre
               JOIN
               w_hier hier
                   ON hier.ifb_id = pre.ifb_parent_id
    ),
    w_hier_non_blank AS
    (
        SELECT
               DISTINCT
               fine_item_code,
               hier_pure_item_path
          FROM w_hier
         WHERE fine_item_code != 'TECH$BLANC'
    )
SELECT
       fine_item_code,
       hier_pure_item_path,
       level,
       ifb_index,
       cnt,
       group_cnct
  FROM (SELECT
               MAX(fine_item_code)                AS fine_item_code,
               MAX(hier_pure_item_path)           AS hier_pure_item_path,
               MIN(level)                         AS level,
               MIN(ifb_index)                     AS ifb_index,
               MIN(ifb_subtotal_flag)             AS ifb_subtotal_flag,
               COUNT(*)                           AS cnt,
               GROUP_CONCAT(file_date_pair, ', ') AS group_cnct
          FROM (SELECT
                       fine_item_code,
                       hier_pure_item_path,
                       level,
                       MAX(ifb_index) OVER (PARTITION BY IFNULL(fine_item_code, hier_pure_item_path)) AS ifb_index,
                       ifb_subtotal_flag,
                       file_date_pair
                  FROM (SELECT
                               IFNULL(NULLIF(hier.fine_item_code, 'TECH$BLANC'), non_blank.fine_item_code) AS fine_item_code,
                               hier.hier_pure_item_path                                                    AS hier_pure_item_path,
                               hier.level                                                                  AS level,
                               hier.ifb_index                                                              AS ifb_index,
                               CASE hier.ifb_subtotal_flag
                                    WHEN 'subtotal' THEN
                                        1
                                    WHEN 'not_subtotal' THEN
                                        0
                               END                                                                         AS ifb_subtotal_flag,
                               hier.file_date || ': ' || hier.ifb_id                                       AS file_date_pair
                          FROM w_hier hier
                               LEFT JOIN
                               w_hier_non_blank non_blank
                                   ON non_blank.hier_pure_item_path = hier.hier_pure_item_path
                                  AND hier.fine_item_code = 'TECH$BLANC'
                         WHERE EXISTS(SELECT
                                             NULL
                                        FROM tbl_emitter
                                       WHERE emitter_id = hier.file_emitter_id
                                         AND emitter_name = :emitter_name)))
         GROUP BY
                  fine_item_code,
                  hier_pure_item_path)
 ORDER BY
          ifb_index,
          ifb_subtotal_flag,
          hier_pure_item_path
