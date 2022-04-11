INSERT
  INTO tbl_item_file_single
  (
    ifs_report_type_id,
    ifs_file_id,
    ifs_item_id,
    ifs_fine_item_id,
    ifs_index
  )
SELECT
       ifs_report_type_id,
       ifs_file_id,
       ifs_item_id,
       ifs_fine_item_id,
       ifs_index
  FROM (SELECT
              report_type.report_type_id AS ifs_report_type_id,
              file.file_id               AS ifs_file_id,
              item.item_id               AS ifs_item_id,
              fine_item.fine_item_id     AS ifs_fine_item_id,
              tmp.item_index             AS ifs_index
         FROM src.tmp_single_dimension_item tmp
              JOIN
              tbl_file file
                  ON file.file_name = tmp.file_name
              JOIN
              tbl_emitter emitter
                  ON emitter.emitter_id = file.file_emitter_id
                 AND emitter.emitter_name = tmp.emitter_name
              JOIN
              tbl_item item
                  ON item.item_name = tmp.item_name
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
                   WHERE ifs_report_type_id = src.ifs_report_type_id
                     AND ifs_file_id        = src.ifs_file_id
                     AND ifs_item_id        = src.ifs_item_id
                     AND ifs_fine_item_id   = src.ifs_fine_item_id
                     AND ifs_index          = src.ifs_index)
  ON CONFLICT(ifs_report_type_id, ifs_file_id, ifs_index)
  DO UPDATE
        SET ifs_report_type_id = excluded.ifs_report_type_id,
            ifs_file_id        = excluded.ifs_file_id,
            ifs_item_id        = excluded.ifs_item_id,
            ifs_fine_item_id   = excluded.ifs_fine_item_id,
            ifs_index          = excluded.ifs_index;
