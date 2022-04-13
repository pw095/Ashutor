INSERT
  INTO tbl_item_file_balance_statistic_snapshot
  (
    ifb_id,
    report_date,
    item_stat_value,
    tech_update_date
  )
SELECT
       ifb.ifb_id,
       tmp.report_date,
       tmp.report_value     AS item_stat_value,
       tmp.tech_update_date
  FROM src.tmp_balance_statistic_snapshot tmp
       JOIN
       tbl_file file
           ON file.file_name = tmp.file_name
       JOIN
       tbl_emitter emitter
           ON emitter.emitter_id = file.file_emitter_id
          AND emitter.emitter_name = tmp.emitter_name
       JOIN
       tbl_item_file_balance ifb
           ON ifb.ifb_index = tmp.item_index
          AND ifb.ifb_file_id = file.file_id
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_balance_statistic_snapshot
                   WHERE ifb_id          = ifb.ifb_id
                     AND report_date     = tmp.report_date
                     AND item_stat_value = tmp.report_value)
 ON CONFLICT(ifb_id, report_date)
 DO UPDATE
       SET item_stat_value  = excluded.item_stat_value,
           tech_update_date = excluded.tech_update_date
