INSERT OR IGNORE
  INTO tbl_emitter
  (
    emitter_name
  )
SELECT
       DISTINCT
       emitent
  FROM src.raw_statement
