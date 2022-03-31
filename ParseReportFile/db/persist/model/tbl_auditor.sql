CREATE TABLE tbl_auditor
  (
    auditor_id   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    auditor_name TEXT                              NOT NULL,
    UNIQUE(auditor_name)
  );
