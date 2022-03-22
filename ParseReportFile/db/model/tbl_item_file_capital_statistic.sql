CREATE TABLE tbl_item_file_capital_statistic
  (
    item_stat_id    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifc_id          INTEGER                           NOT NULL,
    report_date     TEXT                              NOT NULL,
    item_stat_value INTEGER,
    UNIQUE(ifc_id, report_date),
    FOREIGN KEY(ifc_id) REFERENCES tbl_item_file_capital(ifc_id)
  );
