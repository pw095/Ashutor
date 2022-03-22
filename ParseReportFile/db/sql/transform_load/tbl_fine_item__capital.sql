INSERT OR IGNORE
  INTO tbl_fine_item
  (
    fine_item_code
  )
SELECT
       DISTINCT
       fine_item_code
  FROM tmp_fine_item_file_capital_match
