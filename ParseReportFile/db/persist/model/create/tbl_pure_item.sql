CREATE TABLE tbl_pure_item
  (
    pure_item_id     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    pure_item_name   TEXT                              NOT NULL,
    tech_update_date TEXT                              NOT NULL,
    UNIQUE(pure_item_name)
  );
