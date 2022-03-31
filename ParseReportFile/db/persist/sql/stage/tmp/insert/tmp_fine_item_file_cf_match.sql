INSERT
  INTO tmp_fine_item_file_cf_match
  (
    ifcf_id,
    fine_item_code,
    emitter_name,
    file_date
  )
VALUES
  (
    :ifcf_id,
    :fine_item_code,
    :emitter_name,
    :file_date
  )
