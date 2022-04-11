CREATE TABLE tbl_item_file_pl_statistic
  (
    item_stat_id    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifpl_id         INTEGER                           NOT NULL,
    report_date     TEXT                              NOT NULL,
    item_stat_value INTEGER,
    UNIQUE(ifpl_id, report_date),
    FOREIGN KEY(ifpl_id) REFERENCES tbl_item_file_pl(ifpl_id)
  );
