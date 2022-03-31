CREATE TABLE tmp_report_period
  (
    report_period_code TEXT NOT NULL,
    report_period_name TEXT NOT NULL,
    UNIQUE(report_period_code)
  )
