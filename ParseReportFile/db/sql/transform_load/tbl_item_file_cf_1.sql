INSERT OR IGNORE
  INTO tbl_item_file_cf
  (
    ifcf_file_id,
    ifcf_item_id,
    ifcf_fine_item_id,
    ifcf_index
  )
SELECT
       ifcf_file_id,
       ifcf_item_id,
       ifcf_fine_item_id,
       ifcf_index
  FROM tmp_item_file_cf tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_cf
                   WHERE ifcf_file_id = tmp.ifcf_file_id
                     AND ifcf_item_id = tmp.ifcf_item_id)
