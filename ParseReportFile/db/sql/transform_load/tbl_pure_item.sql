INSERT OR IGNORE
  INTO tbl_pure_item
  (
    pure_item_name
  )
SELECT
       item_pure_name
  FROM tmp_item_balance
 UNION
SELECT
       item_pure_name
  FROM tmp_item_pl
 UNION
SELECT
       item_pure_name
  FROM tmp_item_cf
