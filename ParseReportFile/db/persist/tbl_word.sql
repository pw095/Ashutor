INSERT OR ROLLBACK
  INTO tbl_word
  (
    word_name
  )
SELECT
       DISTINCT
       word_name
  FROM tmp_word_info
 WHERE 1 = 1
ON CONFLICT(word_name) DO NOTHING
