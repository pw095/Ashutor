INSERT OR IGNORE
  INTO tbl_item_file_pl
  (
    ifpl_file_id,
    ifpl_item_id,
    ifpl_fine_item_id,
    ifpl_index
  )
SELECT
       ifpl_file_id,
       ifpl_item_id,
       ifpl_fine_item_id,
       ifpl_index
  FROM tmp_item_file_pl tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_pl
                   WHERE ifpl_file_id = tmp.ifpl_file_id
                     AND ifpl_index = tmp.ifpl_index)
