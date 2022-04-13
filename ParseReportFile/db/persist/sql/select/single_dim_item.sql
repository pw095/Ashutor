WITH
     w_ifs AS
     (
         SELECT
                DISTINCT
                ifs.ifs_fine_item_id,
                MAX(item.pure_item_id) OVER (PARTITION BY ifs.ifs_fine_item_id
                                                 ORDER BY ifs.ifs_index
                                             RANGE BETWEEN UNBOUNDED PRECEDING
                                                       AND UNBOUNDED FOLLOWING) AS pure_item_id,
                MAX(ifs.ifs_index)   OVER (PARTITION BY ifs.ifs_fine_item_id
                                               ORDER BY ifs.ifs_index
                                             RANGE BETWEEN UNBOUNDED PRECEDING
                                                       AND UNBOUNDED FOLLOWING) AS ifs_index
           FROM tbl_item_file_single ifs
                JOIN
                tbl_item item
                    ON item.item_id = ifs.ifs_item_id
          WHERE EXISTS(SELECT
                              NULL
                         FROM tbl_report_type report_type
                        WHERE report_type.report_type_id = ifs.ifs_report_type_id
                          AND report_type.report_type_code = :report_type_code)
     )
SELECT
       fine_item.fine_item_code,
       fine_item.fine_item_name,
       pure_item.pure_item_name AS hier_pure_item_path
  FROM w_ifs ifs
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_id = ifs.pure_item_id
       JOIN
       tbl_fine_item fine_item
           ON fine_item.fine_item_id = ifs.ifs_fine_item_id
          AND fine_item.fine_item_code != 'TECH$BLANC'
 ORDER BY
          ifs.ifs_index
