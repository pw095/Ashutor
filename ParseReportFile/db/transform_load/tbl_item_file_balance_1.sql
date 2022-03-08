INSERT OR IGNORE
  INTO tbl_item_file_balance
  (
    ifb_parent_id,
    ifb_file_id,
    ifb_item_id,
    ifb_fine_item_id,
    ifb_level,
    ifb_index,
    ifb_header_flag,
    ifb_subtotal_flag
  )
SELECT
       NULL AS ifb_parent_id,
       ifb_file_id,
       ifb_item_id,
       ifb_fine_item_id,
       ifb_level,
       ifb_index,
       ifb_header_flag,
       ifb_subtotal_flag
  FROM tmp_item_file_balance
