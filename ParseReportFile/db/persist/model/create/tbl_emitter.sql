CREATE TABLE tbl_emitter
  (
    emitter_id       INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    emitter_field_id INTEGER                           NOT NULL,
    emitter_name     TEXT                              NOT NULL,
    tech_update_date TEXT                              NOT NULL,
    UNIQUE(emitter_name),
    FOREIGN KEY(emitter_field_id) REFERENCES tbl_field(field_id) ON DELETE CASCADE
  );
