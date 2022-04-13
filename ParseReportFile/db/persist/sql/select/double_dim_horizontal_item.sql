WITH
     w_ifd AS
     (
         SELECT
                DISTINCT
                ifd.ifd_horizontal_fine_item_id,
                LAST_VALUE(item.pure_item_id)        OVER (PARTITION BY ifd.ifd_horizontal_fine_item_id
                                                               ORDER BY ifd.ifd_horizontal_index) AS pure_item_id,
                LAST_VALUE(ifd.ifd_horizontal_index) OVER (PARTITION BY ifd.ifd_horizontal_fine_item_id
                                                               ORDER BY ifd.ifd_horizontal_index) AS ifd_index
           FROM tbl_item_file_double ifd
                JOIN
                tbl_item item
                    ON item.item_id = ifd.ifd_horizontal_item_id
          WHERE EXISTS(SELECT
                              NULL
                         FROM tbl_report_type report_type
                        WHERE report_type.report_type_id = ifd.ifd_report_type_id
                          AND report_type_code = :report_type_code)
     )
SELECT
       fine_item.fine_item_code,
       fine_item.fine_item_name,
       pure_item.pure_item_name AS hier_pure_item_path
  FROM w_ifd ifd
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_id = ifd.pure_item_id
       JOIN
       tbl_fine_item fine_item
           ON fine_item.fine_item_id = ifd.ifd_horizontal_fine_item_id
          AND fine_item.fine_item_code != 'TECH$BLANC'
 ORDER BY
          ifd.ifd_index
