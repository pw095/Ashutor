CREATE TABLE tbl_item_file_cf_statistic
  (
    item_stat_id    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifcf_id         INTEGER                           NOT NULL,
    report_date     TEXT                              NOT NULL,
    item_stat_value INTEGER,
    UNIQUE(ifcf_id, report_date),
    FOREIGN KEY(ifcf_id) REFERENCES tbl_item_file_cf(ifcf_id)
  );
