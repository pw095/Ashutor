CREATE TABLE tbl_item_file_pl_match
  (
    match_ifpl_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    master_ifpl_id INTEGER                           NOT NULL,
    depend_ifpl_id INTEGER                           NOT NULL,
    UNIQUE(master_ifpl_id, depend_ifpl_id),
    FOREIGN KEY(master_ifpl_id) REFERENCES tbl_item_file_pl(ifpl_id),
    FOREIGN KEY(depend_ifpl_id) REFERENCES tbl_item_file_pl(ifpl_id)
  );
