INSERT
  INTO tbl_item_file_capital_statistic
  (
    ifc_id,
    report_date,
    item_stat_value
  )
SELECT
       ifc.ifc_id,
       tmp.report_date,
       tmp.report_value AS item_stat_value
  FROM tmp_report_capital tmp
       JOIN
       tbl_item_file_capital ifc
           ON ifc.ifc_horizontal_index = tmp.item_horizontal_index
          AND ifc.ifc_vertical_index = tmp.item_vertical_index
       JOIN
       tbl_file file
           ON file.file_id = ifc.ifc_file_id
          AND file.file_name = tmp.file_name
       JOIN
       tbl_emitter emit
           ON emit.emitter_id = file.file_emitter_id
          AND emit.emitter_name = tmp.emitter_name
 WHERE TRUE
 ON CONFLICT(ifc_id, report_date)
 DO UPDATE
       SET item_stat_value = excluded.item_stat_value
     WHERE item_stat_value != excluded.item_stat_value
