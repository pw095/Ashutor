CREATE TABLE tbl_file
  (
    file_id         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    file_name       TEXT                              NOT NULL,
    file_emitter_id INTEGER                           NOT NULL,
    file_date       TEXT                              NOT NULL,
    file_currency   TEXT                              NOT NULL,
    file_factor     TEXT                              NOT NULL,
    UNIQUE(file_name, file_emitter_id),
    UNIQUE(file_date, file_emitter_id),
    FOREIGN KEY(file_emitter_id) REFERENCES tbl_emitter(emitter_id) ON DELETE CASCADE
  );
