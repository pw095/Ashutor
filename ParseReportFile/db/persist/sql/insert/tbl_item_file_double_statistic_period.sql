INSERT
  INTO tbl_item_file_double_statistic_period
  (
    ifd_id,
    report_start_date,
    report_end_date,
    item_stat_value,
    tech_update_date
  )
SELECT
       ifd.ifd_id,
       tmp.report_start_date,
       tmp.report_end_date,
       tmp.report_value      AS item_stat_value,
       tmp.tech_update_date
  FROM src.tmp_double_dimension_statistic_period tmp
       JOIN
       tbl_emitter emitter
           ON emitter.emitter_name = tmp.emitter_name
       JOIN
       tbl_file file
           ON file.file_name = tmp.file_name
          AND file.file_emitter_id = emitter.emitter_id
       JOIN
       tbl_report_type report_type
           ON report_type.report_type_code = tmp.report_type_code
       JOIN
       tbl_item_file_double ifd
           ON ifd.ifd_horizontal_index = tmp.item_horizontal_index
          AND ifd.ifd_vertical_index = tmp.item_vertical_index
          AND ifd.ifd_file_id = file.file_id
          AND ifd.ifd_report_type_id = report_type.report_type_id
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_double_statistic_period
                   WHERE ifd_id            = ifd.ifd_id
                     AND report_start_date = tmp.report_start_date
                     AND report_end_date   = tmp.report_end_date
                     AND item_stat_value   = tmp.report_value)
 ON CONFLICT(ifd_id, report_start_date, report_end_date)
 DO UPDATE
       SET item_stat_value  = excluded.item_stat_value,
           tech_update_date = excluded.tech_update_date
