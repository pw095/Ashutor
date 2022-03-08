CREATE TABLE tbl_item
  (
    item_id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    item_name    TEXT                              NOT NULL,
    pure_item_id INTEGER NOT NULL,
    UNIQUE(item_name),
    FOREIGN KEY(pure_item_id) REFERENCES tbl_pure_item(pure_item_id) ON DELETE CASCADE
  );
