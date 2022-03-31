INSERT
  INTO tmp_fine_item_file_pl_match
  (
    ifpl_id,
    fine_item_code,
    emitter_name,
    file_date
  )
VALUES
  (
    :ifpl_id,
    :fine_item_code,
    :emitter_name,
    :file_date
  )
