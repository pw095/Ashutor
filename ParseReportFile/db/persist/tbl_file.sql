INSERT OR ROLLBACK
  INTO tbl_file
  (
    file_name,
    file_date,
    file_currency,
    file_factor
  )
SELECT
       file_name,
       file_date,
       file_currency,
       file_factor
  FROM tmp_file_info
 WHERE 1 = 1
ON CONFLICT(file_name) DO NOTHING
