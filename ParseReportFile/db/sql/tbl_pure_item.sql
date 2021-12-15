INSERT OR IGNORE
  INTO tbl_pure_item
  (
    pure_item_name
  )
SELECT
       pure_statement
  FROM src.raw_statement
 WHERE statement_number != 0
 UNION
SELECT
       'обязательства' AS pure_statement
