CREATE TABLE tbl_report_period
  (
    report_period_id   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    report_period_code TEXT                              NOT NULL,
    report_period_name TEXT                              NOT NULL,
    tech_update_date   TEXT                              NOT NULL,
    UNIQUE(report_period_code)
  );
