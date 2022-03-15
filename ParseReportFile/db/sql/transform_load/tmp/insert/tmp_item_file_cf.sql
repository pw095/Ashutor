INSERT
  INTO tmp_item_file_cf
  (
    ifcf_file_id,
    ifcf_item_id,
    ifcf_fine_item_id,
    ifcf_index
  )
SELECT
       file.file_id           AS ifcf_file_id,
       item.item_id           AS ifcf_item_id,
       fine_item.fine_item_id AS ifcf_fine_item_id,
       t.item_index           AS ifcf_index
  FROM tmp_item_cf t
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
