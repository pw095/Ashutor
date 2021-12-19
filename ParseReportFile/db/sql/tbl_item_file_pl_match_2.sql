DELETE
  FROM tbl_item_file_pl_match AS dest
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_item_file_pl_match src
               WHERE src.depend_ifpl_id = dest.depend_ifpl_id)
