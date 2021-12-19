CREATE TABLE tbl_item_file_cf_match
  (
    match_ifcf_id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    master_ifcf_id INTEGER                           NOT NULL,
    depend_ifcf_id INTEGER                           NOT NULL,
    UNIQUE(master_ifcf_id, depend_ifcf_id),
    FOREIGN KEY(master_ifcf_id) REFERENCES tbl_item_file_cf(ifcf_id),
    FOREIGN KEY(depend_ifcf_id) REFERENCES tbl_item_file_cf(ifcf_id)
  );
