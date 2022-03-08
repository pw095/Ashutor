INSERT OR IGNORE
  INTO tbl_emitter
  (
    emitter_name
  )
SELECT
       DISTINCT
       emitter_name
  FROM tmp_file_info
