INSERT
  INTO tbl_item_file_pl_statistic
  (
    ifpl_id,
    report_date,
    item_stat_value
  )
SELECT
       ifpl.ifpl_id,
       stmt.report_date,
       ROUND(stmt.item_stat_value) AS item_stat_value
  FROM src.stg_statement_val stmt
       JOIN
       tbl_item_file_pl ifpl
           ON ifpl.ifpl_number = stmt.statement_number
       JOIN
       tbl_file file
           ON file.file_id = ifpl.ifpl_file_id
          AND file.file_name = stmt.file_name
       JOIN
       tbl_emitter emit
           ON emit.emitter_id = file.file_emitter_id
          AND emit.emitter_name = stmt.emitent
 WHERE stmt.sheet = 'PL'
 ON CONFLICT(ifpl_id, report_date)
 DO UPDATE
       SET item_stat_value = excluded.item_stat_value
