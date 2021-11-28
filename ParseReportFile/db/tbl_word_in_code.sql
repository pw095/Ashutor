INSERT OR ROLLBACK
  INTO tbl_word_in_code
  (
    wic_fine_code_id,
    wic_word_id,
    wic_number
  )
SELECT
       fine_code_id AS wic_fine_code_id,
       word_id      AS wic_word_id,
       word_index   AS wic_number
  FROM (SELECT
               DISTINCT
               fine_code_name,
               word_index,
               word_name
          FROM tmp_code_info
               JOIN
               tmp_word_info
                   USING(file_name, code_index))
       JOIN
       tbl_fine_code
           USING(fine_code_name)
       JOIN
       tbl_word
           USING(word_name)
 WHERE 1 = 1
ON CONFLICT(wic_fine_code_id, wic_word_id, wic_number) DO NOTHING
