CREATE TABLE tbl_item_statistic
  (
    item_stat_id    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    isf_id          INTEGER                           NOT NULL,
    report_date     TEXT                              NOT NULL,
    item_stat_value INTEGER,
    UNIQUE(isf_id, report_date),
    FOREIGN KEY(isf_id) REFERENCES tbl_item_sheet_file(isf_id)
  );
