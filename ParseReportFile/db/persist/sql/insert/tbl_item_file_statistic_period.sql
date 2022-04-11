INSERT
  INTO tbl_item_file_single_statistic_period
  (
    ifs_id,
    report_start_date,
    report_end_date,
    item_stat_value,
    tech_update_date
  )
SELECT
       ifs.ifs_id,
       tmp.report_start_date,
       tmp.report_end_date,
       tmp.report_value      AS item_stat_value,
       tmp.tech_update_date
  FROM src.tmp_single_dimension_statistic_period tmp
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
       tbl_item_file_single ifs
           ON ifs.ifs_index = tmp.item_index
          AND ifs.ifs_file_id = file.file_id
          AND ifs.ifs_report_type_id = report_type.report_type_id
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_single_statistic_period
                   WHERE ifs_id            = ifs.ifs_id
                     AND report_start_date = tmp.report_start_date
                     AND report_end_date   = tmp.report_end_date
                     AND item_stat_value   = tmp.report_value)
 ON CONFLICT(ifs_id, report_start_date, report_end_date)
 DO UPDATE
       SET item_stat_value  = excluded.item_stat_value,
           tech_update_date = excluded.tech_update_date
