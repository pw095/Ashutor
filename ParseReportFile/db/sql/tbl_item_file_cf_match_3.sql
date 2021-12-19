INSERT OR IGNORE
  INTO tbl_item_file_cf_match
  (
    master_ifcf_id,
    depend_ifcf_id
  )
SELECT
       master_ifcf_id,
       depend_ifcf_id
  FROM tmp_item_file_cf_match
