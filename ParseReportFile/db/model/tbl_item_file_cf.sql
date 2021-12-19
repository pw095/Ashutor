CREATE TABLE tbl_item_file_cf
  (
    ifcf_id            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifcf_file_id       INTEGER                           NOT NULL,
    ifcf_item_id       INTEGER                           NOT NULL,
    ifcf_fine_item_id  INTEGER                           NOT NULL,
    ifcf_number        REAL                              NOT NULL,
    UNIQUE(ifcf_file_id, ifcf_item_id, ifcf_number),
    FOREIGN KEY(ifcf_file_id)      REFERENCES tbl_file(file_id),
    FOREIGN KEY(ifcf_item_id)      REFERENCES tbl_item(item_id),
    FOREIGN KEY(ifcf_fine_item_id) REFERENCES tbl_fine_item(fine_item_id)
  );
