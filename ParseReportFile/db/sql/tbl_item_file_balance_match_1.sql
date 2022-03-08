DELETE
  FROM tbl_item_file_balance_match AS dest
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_fine_item_match src
               WHERE src.ifb_id = dest.master_ifb_id)
