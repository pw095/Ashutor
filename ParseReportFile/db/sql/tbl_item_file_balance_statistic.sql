INSERT
  INTO tbl_item_file_balance_statistic
  (
    ifb_id,
    report_date,
    item_stat_value
  )
SELECT
       ifb.ifb_id,
       stmt.report_date,
       ROUND(stmt.item_stat_value) AS item_stat_value
  FROM src.stg_statement_val stmt
       JOIN
       tbl_item_file_balance ifb
           ON ifb.ifb_number = stmt.statement_number
       JOIN
       tbl_file file
           ON file.file_id = ifb.ifb_file_id
          AND file.file_name = stmt.file_name
       JOIN
       tbl_emitter emit
           ON emit.emitter_id = file.file_emitter_id
          AND emit.emitter_name = stmt.emitent
 WHERE stmt.sheet = 'BALANCE'
 ON CONFLICT(ifb_id, report_date)
 DO UPDATE
       SET item_stat_value = excluded.item_stat_value
