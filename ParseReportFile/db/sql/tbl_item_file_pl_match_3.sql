INSERT OR IGNORE
  INTO tbl_item_file_pl_match
  (
    master_ifpl_id,
    depend_ifpl_id
  )
SELECT
       master_ifpl_id,
       depend_ifpl_id
  FROM tmp_item_file_pl_match
