DELETE
  FROM tbl_item_file_balance_match dest
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_item_file_balance_match src
               WHERE src.depend_ifb_id = dest.depend_ifb_id)
