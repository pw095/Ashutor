INSERT OR REPLACE
  INTO tbl_code_in_file
  (
    cif_file_id,
    cif_code_id,
    cif_number
  )
SELECT
       DISTINCT
       file_id,
       code_id,
       code_index
  FROM tmp_code_info
       JOIN
       tbl_file
           USING(file_name)
       JOIN
       tbl_code
           USING(code_name)
 WHERE 1 = 1
ON CONFLICT(cif_file_id, cif_code_id, cif_number) DO NOTHING
