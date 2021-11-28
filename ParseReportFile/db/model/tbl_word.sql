CREATE TABLE tbl_word
  (
    word_id   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    word_name TEXT                              NOT NULL,
    UNIQUE(word_name)
  );
