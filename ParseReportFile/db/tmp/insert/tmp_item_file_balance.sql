INSERT
  INTO tmp_item_file_balance
  (
    ifb_file_id,
    ifb_item_id,
    ifb_fine_item_id,
    ifb_level,
    ifb_index,
    ifb_parent_index,
    ifb_header_flag,
    ifb_subtotal_flag
  )
SELECT
       file.file_id           AS ifb_file_id,
       item.item_id           AS ifb_item_id,
       fine_item.fine_item_id AS ifb_fine_item_id,
       t.item_level           AS ifb_level,
       t.item_index           AS ifb_number,
       t.item_parent_index    AS ifb_parent_number,
       t.item_header_flag     AS ifb_header_flag,
       t.item_subtotal_flag   AS ifb_subtotal_flag
  FROM tmp_item_info t
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
