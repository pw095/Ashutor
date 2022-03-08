CREATE TABLE tbl_item_file_balance
  (
    ifb_id            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    ifb_parent_id     INTEGER,
    ifb_file_id       INTEGER                           NOT NULL,
    ifb_item_id       INTEGER                           NOT NULL,
    ifb_fine_item_id  INTEGER                           NOT NULL,
    ifb_level         INTEGER                           NOT NULL,
    ifb_index         INTEGER                           NOT NULL,
    ifb_header_flag   TEXT                              NOT NULL,
    ifb_subtotal_flag TEXT                              NOT NULL,
    FOREIGN KEY(ifb_file_id)      REFERENCES tbl_file(file_id)           ON DELETE CASCADE,
    FOREIGN KEY(ifb_item_id)      REFERENCES tbl_item(item_id)           ON DELETE CASCADE,
    FOREIGN KEY(ifb_fine_item_id) REFERENCES tbl_fine_item(fine_item_id) ON DELETE CASCADE
  );
