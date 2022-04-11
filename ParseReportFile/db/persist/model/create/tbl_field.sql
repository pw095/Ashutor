CREATE TABLE tbl_field
  (
    field_id         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    field_name       TEXT                              NOT NULL,
    tech_update_date TEXT                              NOT NULL,
    UNIQUE(field_name)
  );
