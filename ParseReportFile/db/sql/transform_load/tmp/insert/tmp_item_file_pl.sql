INSERT
  INTO tmp_item_file_pl
  (
    ifpl_file_id,
    ifpl_item_id,
    ifpl_fine_item_id,
    ifpl_index
  )
SELECT
       file.file_id           AS ifpl_file_id,
       item.item_id           AS ifpl_item_id,
       fine_item.fine_item_id AS ifpl_fine_item_id,
       t.item_index           AS ifpl_index
  FROM tmp_item_pl t
       JOIN
       tbl_emitter emit
           ON emit.emitter_name = t.emitter_name
       JOIN
       tbl_file file
           ON file.file_name = t.file_name
          AND file.file_emitter_id = emit.emitter_id
       JOIN
       tbl_item item
           ON item.item_name = t.item_name
       CROSS JOIN
       tbl_fine_item fine_item
 WHERE fine_item.fine_item_code = 'TECH$BLANC'
