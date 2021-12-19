INSERT OR IGNORE
  INTO tbl_item_file_pl
  (
    ifpl_file_id,
    ifpl_item_id,
    ifpl_fine_item_id,
    ifpl_number
  )
SELECT
       file.file_id           AS ifpl_file_id,
       item.item_id           AS ifpl_item_id,
       fine_item.fine_item_id AS ifpl_fine_item_id,
       stmt.statement_number  AS ifpl_number
  FROM src.stg_statement stmt
       JOIN
       tbl_file file
           ON file.file_name = stmt.file_name
       JOIN
       tbl_item item
           ON item.item_name = stmt.statement
       CROSS JOIN
       tbl_fine_item fine_item
 WHERE stmt.statement_number != 0
   AND stmt.sheet = 'PL'
   AND fine_item.fine_item_name = 'TECH$BLANC'
   AND EXISTS(SELECT
                     NULL
                FROM tbl_emitter emit
               WHERE emit.emitter_id = file.file_emitter_id
                 AND emit.emitter_name = stmt.emitent)
