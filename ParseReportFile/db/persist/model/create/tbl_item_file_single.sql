CREATE TABLE tbl_item_file_single
  (
    ifs_id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifs_report_type_id INTEGER                           NOT NULL,
    ifs_file_id        INTEGER                           NOT NULL,
    ifs_item_id        INTEGER                           NOT NULL,
    ifs_fine_item_id   INTEGER                           NOT NULL,
    ifs_index          INTEGER                           NOT NULL,
    UNIQUE(ifs_report_type_id, ifs_file_id, ifs_index),
    FOREIGN KEY(ifs_file_id)      REFERENCES tbl_file(file_id),
    FOREIGN KEY(ifs_item_id)      REFERENCES tbl_item(item_id),
    FOREIGN KEY(ifs_fine_item_id) REFERENCES tbl_fine_item(fine_item_id)
  );
