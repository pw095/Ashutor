DELETE
  FROM tbl_item_file_pl_match AS dest
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_item_file_pl_match src
               WHERE src.master_ifpl_id = dest.master_ifpl_id)
