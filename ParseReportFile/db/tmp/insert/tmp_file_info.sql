INSERT
  INTO tmp_file_info
  (
    emitter_name,
    file_name,
    file_date,
    file_currency,
    file_factor
  )
VALUES
  (
    :emitter_name,
    :file_name,
    :file_date,
    :file_currency,
    :file_factor
  )
