CREATE TABLE tbl_sheet
  (
    sheet_id   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    sheet_code TEXT                              NOT NULL,
    sheet_name TEXT                              NOT NULL,
    UNIQUE(sheet_code)
  );
