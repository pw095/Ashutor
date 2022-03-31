INSERT OR REPLACE
  INTO tbl_statistic
  (
    cif_id,
    report_date,
    stat_value
  )
SELECT
       cif.cif_id,
       report_year,
       report_value AS stat_value
  FROM tmp_report_info
       JOIN
       tmp_code_info
           USING(file_name, code_index)
       JOIN
       tbl_file file
           USING(file_name)
       JOIN
       tbl_code code
           USING(code_name)
       JOIN
       tbl_code_in_file cif
           ON cif.cif_code_id = code.code_id
          AND cif.cif_file_id = file.file_id
