INSERT OR REPLACE
  INTO tbl_code
  (
    code_name,
    fine_code_id
  )
SELECT
       code_name,
       fine_code_id
  FROM (SELECT
               DISTINCT
               code_name,
               fine_code_name
          FROM tmp_code_info)
       JOIN
       tbl_fine_code
           USING(fine_code_name)
 WHERE 1 = 1
ON CONFLICT(code_name) DO NOTHING
