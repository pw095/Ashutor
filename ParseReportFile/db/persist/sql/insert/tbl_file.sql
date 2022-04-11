INSERT
  INTO tbl_file
    (
      file_name,
      file_emitter_id,
      file_report_period_id,
      file_auditor_id,
      file_date,
      file_publish_date,
      file_currency,
      file_factor,
      tech_update_date
    )
SELECT
       tmp.file_name,
       emitter.emitter_id             AS file_emitter_id,
       report_period.report_period_id AS file_report_period_id,
       auditor.auditor_id             AS file_auditor_id,
       tmp.file_date,
       tmp.publish_date               AS file_publish_date,
       tmp.currency                   AS file_currency,
       tmp.factor                     AS file_factor,
       tmp.tech_update_date
  FROM src.tmp_ref_info tmp
       JOIN
       tbl_auditor auditor
           ON auditor.auditor_name = tmp.auditor_name
       JOIN
       tbl_emitter emitter
           ON emitter.emitter_name = tmp.emitter_name
       JOIN
       tbl_report_period report_period
           ON report_period.report_period_code = tmp.report_period_code
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_file
                   WHERE file_name             = tmp.file_name
                     AND file_emitter_id       = emitter.emitter_id
                     AND file_report_period_id = report_period.report_period_id
                     AND file_auditor_id       = auditor.auditor_id
                     AND file_date             = tmp.file_date
                     AND file_publish_date     = tmp.publish_date
                     AND file_currency         = tmp.currency
                     AND file_factor           = tmp.factor)
  ON CONFLICT(file_name, file_emitter_id)
  DO UPDATE
        SET file_report_period_id = excluded.file_report_period_id,
            file_auditor_id       = excluded.file_auditor_id,
            file_date             = excluded.file_date,
            file_publish_date     = excluded.file_publish_date,
            file_currency         = excluded.file_currency,
            file_factor           = excluded.file_factor,
            tech_update_date      = excluded.tech_update_date;
