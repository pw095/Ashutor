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
  FROM tmp_item_file_balance tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_balance
                   WHERE ifb_file_id = tmp.ifb_file_id
                     AND ifb_item_id = tmp.ifb_item_id)
