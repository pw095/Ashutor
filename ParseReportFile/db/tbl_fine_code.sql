INSERT OR ROLLBACK
  INTO tbl_fine_code
  (
    fine_code_name
  )
SELECT
       DISTINCT
       fine_code_name
  FROM tmp_code_info
 WHERE 1 = 1
ON CONFLICT(fine_code_name) DO NOTHING
