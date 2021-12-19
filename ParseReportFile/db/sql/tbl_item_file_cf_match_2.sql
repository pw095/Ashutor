DELETE
  FROM tbl_item_file_cf_match AS dest
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_item_file_cf_match src
               WHERE src.depend_ifcf_id = dest.depend_ifcf_id)
