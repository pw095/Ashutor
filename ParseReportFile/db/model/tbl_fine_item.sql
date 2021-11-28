CREATE TABLE tbl_fine_item
  (
    fine_item_id   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    fine_item_name TEXT                              NOT NULL,
    UNIQUE(fine_item_name)
  );
