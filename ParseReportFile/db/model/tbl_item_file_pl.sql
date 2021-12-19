CREATE TABLE tbl_item_file_pl
  (
    ifpl_id            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifpl_file_id       INTEGER                           NOT NULL,
    ifpl_item_id       INTEGER                           NOT NULL,
    ifpl_fine_item_id  INTEGER                           NOT NULL,
    ifpl_number        REAL                              NOT NULL,
    UNIQUE(ifpl_file_id, ifpl_item_id, ifpl_number),
    FOREIGN KEY(ifpl_file_id)      REFERENCES tbl_file(file_id),
    FOREIGN KEY(ifpl_item_id)      REFERENCES tbl_item(item_id),
    FOREIGN KEY(ifpl_fine_item_id) REFERENCES tbl_fine_item(fine_item_id)
  );
