WITH
     w_pre AS
     (
         SELECT
                file_emitter_id,
                file_date,
                ifc_id,
                fine_item_code,
                ifc_index,
                pure_item_name,
                rn
           FROM (SELECT
                        file.file_emitter_id,
                        file.file_date,
                        ifc.ifc_id,
                        fine_item.fine_item_code,
                        ifc.ifc_horizontal_index AS ifc_index,
                        pure_item.pure_item_name,
                        ROW_NUMBER() OVER (PARTITION BY
                                                        file.file_emitter_id,
                                                        file.file_date,
                                                        fine_item.fine_item_code,
                                                        pure_item.pure_item_name
                                               ORDER BY
                                                        DATE(ifcs.report_date) DESC) AS rn
                   FROM tbl_item_file_capital ifc
                        JOIN
                        tbl_item item
                            ON item.item_id = ifc.ifc_horizontal_item_id
                        JOIN
                        tbl_pure_item pure_item
                            ON pure_item.pure_item_id = item.pure_item_id
                        JOIN
                        tbl_file file
                            ON file.file_id = ifc.ifc_file_id
                        LEFT JOIN
                        tbl_fine_item fine_item
                            ON fine_item.fine_item_id = ifc.ifc_horizontal_fine_item_id
                        JOIN
                        tbl_item_file_capital_statistic ifcs
                            ON ifcs.ifc_id = ifc.ifc_id)
          WHERE rn = 1
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
       MAX(ifc_index)                     AS ifc_index,
       COUNT(*)                           AS cnt,
       GROUP_CONCAT(file_date_pair, ', ') AS group_cnct
  FROM (SELECT
               IFNULL(fine_item_code, pure_item_name) AS group_by_expr,
               fine_item_code,
               pure_item_name,
               file_date_pair,
               ifc_index
          FROM (SELECT
                       IFNULL(NULLIF(pre.fine_item_code, 'TECH$BLANC'), non_blank.fine_item_code) AS fine_item_code,
                       pre.pure_item_name                                                         AS pure_item_name,
                       pre.ifc_index                                                              AS ifc_index,
                       pre.file_date || ': ' || pre.ifc_id                                        AS file_date_pair
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
          ifc_index,
          pure_item_name
