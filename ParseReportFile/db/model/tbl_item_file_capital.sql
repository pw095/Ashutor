CREATE TABLE tbl_item_file_capital
  (
    ifc_id                      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifc_file_id                 INTEGER                           NOT NULL,
    ifc_horizontal_item_id      INTEGER                           NOT NULL,
    ifc_horizontal_fine_item_id INTEGER                           NOT NULL,
    ifc_horizontal_index        INTEGER                           NOT NULL,
    ifc_vertical_item_id        INTEGER                           NOT NULL,
    ifc_vertical_fine_item_id   INTEGER                           NOT NULL,
    ifc_vertical_index          INTEGER                           NOT NULL,
    UNIQUE(ifc_file_id, ifc_horizontal_index, ifc_vertical_index),
    FOREIGN KEY(ifc_file_id)                 REFERENCES tbl_file(file_id),
    FOREIGN KEY(ifc_horizontal_item_id)      REFERENCES tbl_item(item_id),
    FOREIGN KEY(ifc_horizontal_fine_item_id) REFERENCES tbl_fine_item(fine_item_id),
    FOREIGN KEY(ifc_vertical_item_id)        REFERENCES tbl_item(item_id),
    FOREIGN KEY(ifc_vertical_fine_item_id)   REFERENCES tbl_fine_item(fine_item_id)
  );
