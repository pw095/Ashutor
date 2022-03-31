INSERT
  INTO tbl_report_period
  (
    report_period_code,
    report_period_name
  )
SELECT
       report_period_code,
       report_period_name
  FROM src.tmp_report_period tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_report_period
                   WHERE report_period_code = tmp.report_period_code
                     AND report_period_name = tmp.report_period_name)
  ON CONFLICT(report_period_code)
  DO UPDATE
        SET report_period_name = excluded.report_period_name
