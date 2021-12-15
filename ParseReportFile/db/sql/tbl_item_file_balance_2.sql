UPDATE tbl_item_file_balance AS dest
   SET ifb_parent_id = (SELECT
                               ifb_parent.ifb_id
                          FROM tmp_item_file_balance src
                               JOIN
                               tbl_item_file_balance ifb_parent
                                   ON ifb_parent.ifb_file_id = src.ifb_file_id
                                  AND ifb_parent.ifb_number = src.ifb_parent_number
                         WHERE src.ifb_file_id = dest.ifb_file_id
                           AND src.ifb_number = dest.ifb_number)
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_item_file_balance src
               WHERE src.ifb_file_id = dest.ifb_file_id
                 AND src.ifb_number = dest.ifb_number)
