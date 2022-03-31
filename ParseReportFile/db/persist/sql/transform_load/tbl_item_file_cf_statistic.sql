INSERT
  INTO tbl_item_file_cf_statistic
  (
    ifcf_id,
    report_date,
    item_stat_value
  )
SELECT
       ifcf.ifcf_id,
       tmp.report_date,
       ROUND(tmp.report_value) AS item_stat_value
  FROM tmp_report_cf tmp
       JOIN
       tbl_item_file_cf ifcf
           ON ifcf.ifcf_index = tmp.item_index
       JOIN
       tbl_file file
           ON file.file_id = ifcf.ifcf_file_id
          AND file.file_name = tmp.file_name
       JOIN
       tbl_emitter emit
           ON emit.emitter_id = file.file_emitter_id
          AND emit.emitter_name = tmp.emitter_name
 WHERE TRUE
 ON CONFLICT(ifcf_id, report_date)
 DO UPDATE
       SET item_stat_value = excluded.item_stat_value
     WHERE item_stat_value != excluded.item_stat_value
