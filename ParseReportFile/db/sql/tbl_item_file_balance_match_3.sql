INSERT OR IGNORE
  INTO tbl_item_file_balance_match
  (
    master_ifb_id,
    depend_ifb_id
  )
SELECT
       master_ifb_id,
       depend_ifb_id
  FROM tmp_item_file_balance_match
