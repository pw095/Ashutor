DELETE
  FROM tbl_item_file_balance_match AS dest
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_item_file_balance_match src
               WHERE src.master_ifb_id = dest.master_ifb_id)
