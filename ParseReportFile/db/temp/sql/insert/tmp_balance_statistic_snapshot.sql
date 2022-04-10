INSERT
  INTO tmp_balance_statistic_snapshot
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
