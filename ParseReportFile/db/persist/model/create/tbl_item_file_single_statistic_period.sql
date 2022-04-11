CREATE TABLE tbl_item_file_single_statistic_period
  (
    item_stat_id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifs_id            INTEGER                           NOT NULL,
    report_start_date TEXT                              NOT NULL,
    report_end_date   TEXT                              NOT NULL,
    item_stat_value   INTEGER                           NOT NULL,
    tech_update_date  TEXT                              NOT NULL,
    UNIQUE(ifs_id, report_start_date, report_end_date),
    FOREIGN KEY(ifs_id) REFERENCES tbl_item_file_single(ifs_id)
  );
