CREATE TABLE tbl_word_in_item
  (
    wii_id           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    wii_pure_item_id INTEGER                           NOT NULL,
    wii_word_id      INTEGER                           NOT NULL,
    wii_number       INTEGER                           NOT NULL,
    UNIQUE(wii_pure_item_id, wii_word_id, wii_number),
    FOREIGN KEY(wii_pure_item_id) REFERENCES tbl_pure_item(pure_item_id) ON DELETE CASCADE,
    FOREIGN KEY(wii_number)       REFERENCES tbl_word(word_id)           ON DELETE CASCADE
  );
