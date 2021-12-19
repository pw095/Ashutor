INSERT OR IGNORE
  INTO tbl_item
  (
    item_name,
    pure_item_id
  )
SELECT
       stmt.statement AS item_name,
       pure_item.pure_item_id
  FROM (SELECT
               statement,
               pure_statement
          FROM src.stg_statement
         WHERE statement_number != 0
         UNION
        SELECT
               'Обязательства' AS statement,
               'обязательства' AS pure_statement) stmt
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_name = stmt.pure_statement
