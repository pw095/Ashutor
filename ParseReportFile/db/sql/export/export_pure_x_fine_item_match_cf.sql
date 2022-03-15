SELECT
       MAX(fine_item_code)                AS fine_item_code,
       MAX(pure_item_name)                AS hier_pure_item_path,
       MAX(ifcf_index)                    AS ifcf_index,
       COUNT(*)                           AS cnt,
       GROUP_CONCAT(file_date_pair, ', ') AS group_cnct
  FROM (SELECT
               IFNULL(fine_item.fine_item_code, pure_item.pure_item_name) AS group_by_expr,
               fine_item.fine_item_code,
               pure_item.pure_item_name,
               file.file_date || ': ' || ifcf.ifcf_id                     AS file_date_pair,
               ifcf.ifcf_index                                            AS ifcf_index
          FROM tbl_item_file_cf ifcf
               JOIN
               tbl_item item
                   ON item.item_id = ifcf.ifcf_item_id
               JOIN
               tbl_pure_item pure_item
                   ON pure_item.pure_item_id = item.pure_item_id
               JOIN
               tbl_file file
                   ON file.file_id = ifcf.ifcf_file_id
               LEFT JOIN
               tbl_fine_item fine_item
                   ON fine_item.fine_item_id = ifcf.ifcf_fine_item_id
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
          ifcf_index,
          pure_item_name
