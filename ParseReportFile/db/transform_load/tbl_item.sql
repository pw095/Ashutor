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
          FROM tmp_item_info) stmt
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_name = stmt.item_pure_name
