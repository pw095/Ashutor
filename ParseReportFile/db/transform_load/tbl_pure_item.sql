INSERT OR IGNORE
  INTO tbl_pure_item
  (
    pure_item_name
  )
SELECT
       DISTINCT
       item_pure_name
  FROM tmp_item_info
