INSERT
  INTO tmp_fine_item_file_capital_match
  (
    ifc_id,
    fine_item_code,
    emitter_name,
    file_date
  )
VALUES
  (
    :ifc_id,
    :fine_item_code,
    :emitter_name,
    :file_date
  )
