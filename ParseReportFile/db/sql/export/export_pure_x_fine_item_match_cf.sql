WITH
     w_pre AS
     (
         SELECT
                file.file_emitter_id,
                file.file_date,
                ifcf.ifcf_id,
                fine_item.fine_item_code,
                ifcf.ifcf_index,
                pure_item.pure_item_name
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
     ),
     w_non_blank AS
     (
         SELECT
                DISTINCT
                fine_item_code,
                pure_item_name
           FROM w_pre pre
          WHERE fine_item_code != 'TECH$BLANC'
     )
SELECT
       MAX(fine_item_code)                AS fine_item_code,
       MAX(pure_item_name)                AS hier_pure_item_path,
       MAX(ifcf_index)                    AS ifcf_index,
       COUNT(*)                           AS cnt,
       GROUP_CONCAT(file_date_pair, ', ') AS group_cnct
  FROM (SELECT
               IFNULL(fine_item_code, pure_item_name) AS group_by_expr,
               fine_item_code,
               pure_item_name,
               file_date_pair,
               ifcf_index
          FROM (SELECT
                       IFNULL(NULLIF(pre.fine_item_code, 'TECH$BLANC'), non_blank.fine_item_code) AS fine_item_code,
                       pre.pure_item_name                                                         AS pure_item_name,
                       pre.ifcf_index                                                             AS ifcf_index,
                       pre.file_date || ': ' || pre.ifcf_id                                       AS file_date_pair
                  FROM w_pre pre
                       LEFT JOIN
                       w_non_blank non_blank
                           ON non_blank.pure_item_name = pre.pure_item_name
                          AND pre.fine_item_code = 'TECH$BLANC'
                 WHERE EXISTS(SELECT
                                     NULL
                                FROM tbl_emitter
                               WHERE emitter_id = pre.file_emitter_id
                                 AND emitter_name = :emitter_name)))
 GROUP BY
          fine_item_code,
          pure_item_name
 ORDER BY
          ifcf_index,
          pure_item_name
