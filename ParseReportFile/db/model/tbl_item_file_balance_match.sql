CREATE TABLE tbl_item_file_balance_match
  (
    match_ifb_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    master_ifb_id INTEGER                           NOT NULL,
    depend_ifb_id INTEGER                           NOT NULL,
    UNIQUE(master_ifb_id, depend_ifb_id),
    FOREIGN KEY(master_ifb_id) REFERENCES tbl_item_file_balance(ifb_id),
    FOREIGN KEY(depend_ifb_id) REFERENCES tbl_item_file_balance(ifb_id)
  );
