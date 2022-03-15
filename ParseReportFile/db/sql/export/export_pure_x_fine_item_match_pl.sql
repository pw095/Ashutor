SELECT
       MAX(fine_item_code)                AS fine_item_code,
       MAX(pure_item_name)                AS hier_pure_item_path,
       MAX(ifpl_index)                    AS ifpl_index,
       COUNT(*)                           AS cnt,
       GROUP_CONCAT(file_date_pair, ', ') AS group_cnct
  FROM (SELECT
               IFNULL(fine_item.fine_item_code, pure_item.pure_item_name) AS group_by_expr,
               fine_item.fine_item_code,
               pure_item.pure_item_name,
               file.file_date || ': ' || ifpl.ifpl_id                     AS file_date_pair,
               ifpl.ifpl_index                                            AS ifpl_index
          FROM tbl_item_file_pl ifpl
               JOIN
               tbl_item item
                   ON item.item_id = ifpl.ifpl_item_id
               JOIN
               tbl_pure_item pure_item
                   ON pure_item.pure_item_id = item.pure_item_id
               JOIN
               tbl_file file
                   ON file.file_id = ifpl.ifpl_file_id
               LEFT JOIN
               tbl_fine_item fine_item
                   ON fine_item.fine_item_id = ifpl.ifpl_fine_item_id
                  AND fine_item.fine_item_code != 'TECH$BLANC'
         WHERE EXISTS(SELECT
                             NULL
                        FROM tbl_emitter
                       WHERE emitter_id = file.file_emitter_id
                         AND emitter_name = :emitter_name))
 GROUP BY
          fine_item_code,
          pure_item_name
 ORDER BY
          ifpl_index,
          pure_item_name
