INSERT
  INTO tmp_report_capital
  (
    emitter_name,
    file_name,
    item_horizontal_index,
    item_vertical_index,
    report_date,
    report_value
  )
VALUES
  (
    :emitter_name,
    :file_name,
    :item_horizontal_index,
    :item_vertical_index,
    :report_date,
    :report_value
  )
