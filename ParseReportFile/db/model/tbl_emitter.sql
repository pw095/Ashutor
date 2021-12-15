CREATE TABLE tbl_emitter
  (
    emitter_id   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    emitter_name TEXT                              NOT NULL,
    UNIQUE(emitter_name)
  );
