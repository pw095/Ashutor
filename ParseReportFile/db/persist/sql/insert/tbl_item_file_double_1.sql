INSERT
  INTO tbl_item_file_double
  (
    ifd_report_type_id,
    ifd_file_id,
    ifd_horizontal_item_id,
    ifd_horizontal_fine_item_id,
    ifd_horizontal_index,
    ifd_vertical_item_id,
    ifd_vertical_fine_item_id,
    ifd_vertical_index,
    tech_update_date
  )
SELECT
       ifd_report_type_id,
       ifd_file_id,
       ifd_horizontal_item_id,
       ifd_horizontal_fine_item_id,
       ifd_horizontal_index,
       ifd_vertical_item_id,
       ifd_vertical_fine_item_id,
       ifd_vertical_index,
       tech_update_date
  FROM (SELECT
              report_type.report_type_id AS ifd_report_type_id,
              file.file_id               AS ifd_file_id,
              h_item.item_id             AS ifd_horizontal_item_id,
              fine_item.fine_item_id     AS ifd_horizontal_fine_item_id,
              tmp.item_horizontal_index  AS ifd_horizontal_index,
              v_item.item_id             AS ifd_vertical_item_id,
              fine_item.fine_item_id     AS ifd_vertical_fine_item_id,
              tmp.item_vertical_index    AS ifd_vertical_index,
              tmp.tech_update_date
         FROM src.tmp_double_dimension_item tmp
              JOIN
              tbl_emitter emitter
                  ON emitter.emitter_name = tmp.emitter_name
              JOIN
              tbl_file file
                  ON file.file_name = tmp.file_name
                 AND file.file_emitter_id = emitter.emitter_id
              JOIN
              tbl_item h_item
                  ON h_item.item_name = tmp.item_horizontal_name
              JOIN
              tbl_item v_item
                  ON v_item.item_name = tmp.item_vertical_name
              JOIN
              tbl_report_type report_type
                  ON report_type.report_type_code = tmp.report_type_code
              CROSS JOIN
              (SELECT
                      fine_item_id
                 FROM tbl_fine_item
                WHERE fine_item_code = 'TECH$BLANC') fine_item) src
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_single
                   WHERE ifd_report_type_id          = src.ifd_report_type_id
                     AND ifd_file_id                 = src.ifd_file_id
                     AND ifd_horizontal_item_id      = src.ifd_horizontal_item_id
                     AND ifd_horizontal_fine_item_id = src.ifd_horizontal_fine_item_id
                     AND ifd_horizontal_index        = src.ifd_horizontal_index
                     AND ifd_vertical_item_id        = src.ifd_vertical_item_id
                     AND ifd_vertical_fine_item_id   = src.ifd_vertical_fine_item_id
                     AND ifd_vertical_index          = src.ifd_vertical_index)
  ON CONFLICT(ifd_report_type_id, ifd_file_id, ifd_horizontal_index, ifd_vertical_index)
  DO UPDATE
        SET ifd_report_type_id          = excluded.ifd_report_type_id,
            ifd_file_id                 = excluded.ifd_file_id,
            ifd_horizontal_item_id      = excluded.ifd_horizontal_item_id,
            ifd_horizontal_fine_item_id = excluded.ifd_horizontal_fine_item_id,
            ifd_horizontal_index        = excluded.ifd_horizontal_index,
            ifd_vertical_item_id        = excluded.ifd_vertical_item_id,
            ifd_vertical_fine_item_id   = excluded.ifd_vertical_fine_item_id,
            ifd_vertical_index          = excluded.ifd_vertical_index,
            tech_update_date            = excluded.tech_update_date
