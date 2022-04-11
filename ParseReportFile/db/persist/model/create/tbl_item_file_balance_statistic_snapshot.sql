CREATE TABLE tbl_item_file_balance_statistic_snapshot
  (
    item_stat_id     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifb_id           INTEGER                           NOT NULL,
    report_date      TEXT                              NOT NULL,
    item_stat_value  INTEGER,
    tech_update_date TEXT,
    UNIQUE(ifb_id, report_date),
    FOREIGN KEY(ifb_id) REFERENCES tbl_item_file_balance(ifb_id) ON DELETE CASCADE
  );
