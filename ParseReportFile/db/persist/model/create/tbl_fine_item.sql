CREATE TABLE tbl_fine_item
  (
    fine_item_id     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    fine_item_code   TEXT                              NOT NULL,
    fine_item_name   TEXT,
    tech_update_date TEXT                              NOT NULL,
    UNIQUE(fine_item_code)
  );
