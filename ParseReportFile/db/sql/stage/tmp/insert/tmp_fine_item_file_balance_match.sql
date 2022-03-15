INSERT
  INTO tmp_fine_item_file_balance_match
  (
    ifb_id,
    fine_item_code,
    emitter_name,
    file_date
  )
VALUES
  (
    :ifb_id,
    :fine_item_code,
    :emitter_name,
    :file_date
  )
