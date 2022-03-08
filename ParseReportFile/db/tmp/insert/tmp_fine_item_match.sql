INSERT
  INTO tmp_fine_item_match
  (
    row_index,
    fine_item_code,
    emitter_name,
    file_date,
    ifb_id
  )
VALUES
  (
    :row_index,
    :fine_item_code,
    :emitter_name,
    :file_date,
    :ifb_id
  )
