INSERT
  INTO tbl_report_period
  (
    report_period_code,
    report_period_name,
    tech_update_date
  )
SELECT
       report_period_code,
       report_period_code AS report_period_name,
       tech_update_date
  FROM src.tmp_ref_info tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_report_period
                   WHERE report_period_code = tmp.report_period_code
                     AND report_period_name = tmp.report_period_code)
  ON CONFLICT(report_period_code)
  DO UPDATE
        SET report_period_name = excluded.report_period_name,
            tech_update_date = excluded.tech_update_date
