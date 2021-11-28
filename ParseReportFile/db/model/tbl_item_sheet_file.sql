CREATE TABLE tbl_item_sheet_file
  (
    isf_id           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    isf_file_id      INTEGER                           NOT NULL,
    isf_sheet_id     INTEGER                           NOT NULL,
    isf_item_id      INTEGER                           NOT NULL,
    isf_fine_item_id INTEGER                           NOT NULL,
    isf_level        INTEGER                           NOT NULL,
    isf_number       INTEGER                           NOT NULL,
    UNIQUE(isf_file_id, isf_sheet_id, isf_item_id, isf_number),
    FOREIGN KEY(isf_file_id)      REFERENCES tbl_file(file_id),
    FOREIGN KEY(isf_sheet_id)     REFERENCES tbl_sheet(sheet_id),
    FOREIGN KEY(isf_item_id)      REFERENCES tbl_item(item_id),
    FOREIGN KEY(isf_fine_item_id) REFERENCES tbl_fine_item(fine_item_id)
  );
