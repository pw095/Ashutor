INSERT
  INTO tbl_file
  (
    file_name,
    file_emitter_id,
    file_date,
    file_currency,
    file_factor
  )
SELECT
       file.file_name,
       emit.emitter_id    AS file_emitter_id,
       file.file_date,
       file.file_currency,
       file.file_factor
  FROM tmp_file file
       JOIN
       tbl_emitter emit
           ON emit.emitter_name = file.emitter_name
 WHERE 1 = 1
 ON CONFLICT(file_name, file_emitter_id)
 DO UPDATE
       SET file_date     = excluded.file_date,
           file_currency = excluded.file_currency,
           file_factor   = excluded.file_factor
