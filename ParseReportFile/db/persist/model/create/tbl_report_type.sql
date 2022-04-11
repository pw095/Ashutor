CREATE TABLE tbl_report_type
  (
    report_type_id   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    report_type_code TEXT                              NOT NULL,
    report_type_name TEXT                              NOT NULL,
    tech_update_date TEXT                              NOT NULL,
    UNIQUE(report_type_code)
  );
