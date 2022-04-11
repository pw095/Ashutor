CREATE TABLE tbl_item_file_double
  (
    ifd_id                      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifd_report_type_id          INTEGER                           NOT NULL,
    ifd_file_id                 INTEGER                           NOT NULL,
    ifd_horizontal_item_id      INTEGER                           NOT NULL,
    ifd_horizontal_fine_item_id INTEGER                           NOT NULL,
    ifd_horizontal_index        INTEGER                           NOT NULL,
    ifd_vertical_item_id        INTEGER                           NOT NULL,
    ifd_vertical_fine_item_id   INTEGER                           NOT NULL,
    ifd_vertical_index          INTEGER                           NOT NULL,
    tech_update_date            TEXT                              NOT NULL,
    UNIQUE(ifd_report_type_id, ifd_file_id, ifd_horizontal_index, ifd_vertical_index),
    FOREIGN KEY(ifd_report_type_id)          REFERENCES tbl_report_type(report_type_id),
    FOREIGN KEY(ifd_file_id)                 REFERENCES tbl_file(file_id),
    FOREIGN KEY(ifd_horizontal_item_id)      REFERENCES tbl_item(item_id),
    FOREIGN KEY(ifd_horizontal_fine_item_id) REFERENCES tbl_fine_item(fine_item_id),
    FOREIGN KEY(ifd_vertical_item_id)        REFERENCES tbl_item(item_id),
    FOREIGN KEY(ifd_vertical_fine_item_id)   REFERENCES tbl_fine_item(fine_item_id)
  );
