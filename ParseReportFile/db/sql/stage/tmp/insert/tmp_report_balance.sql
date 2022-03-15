INSERT
  INTO tmp_report_balance
  (
    emitter_name,
    file_name,
    item_index,
    report_date,
    report_value
  )
VALUES
  (
    :emitter_name,
    :file_name,
    :item_index,
    :report_date,
    :report_value
  )
