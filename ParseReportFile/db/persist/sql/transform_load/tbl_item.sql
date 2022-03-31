INSERT OR IGNORE
  INTO tbl_item
  (
    item_name,
    pure_item_id
  )
SELECT
       stmt.item_name,
       pure_item.pure_item_id
  FROM (SELECT
               item_name,
               item_pure_name
          FROM tmp_item_balance
         UNION
        SELECT
               item_name,
               item_pure_name
          FROM tmp_item_pl
         UNION
        SELECT
               item_name,
               item_pure_name
          FROM tmp_item_cf
         UNION
        SELECT
               item_horizontal_name,
               item_horizontal_pure_name
          FROM tmp_item_capital
         UNION
        SELECT
               item_vertical_name,
               item_vertical_pure_name
          FROM tmp_item_capital) stmt
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_name = stmt.item_pure_name
