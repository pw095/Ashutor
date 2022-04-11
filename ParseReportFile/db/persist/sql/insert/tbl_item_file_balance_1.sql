INSERT
  INTO tbl_item_file_balance
  (
    ifb_parent_id,
    ifb_file_id,
    ifb_item_id,
    ifb_fine_item_id,
    ifb_level,
    ifb_index,
    ifb_header_flag,
    ifb_subtotal_flag,
    tech_update_date
  )
SELECT
       ifb_parent_id,
       ifb_file_id,
       ifb_item_id,
       ifb_fine_item_id,
       ifb_level,
       ifb_index,
       ifb_header_flag,
       ifb_subtotal_flag,
       tech_update_date
  FROM (SELECT
               NULL                   AS ifb_parent_id,
               file.file_id           AS ifb_file_id,
               item.item_id           AS ifb_item_id,
               fine_item.fine_item_id AS ifb_fine_item_id,
               tmp.item_level         AS ifb_level,
               tmp.item_index         AS ifb_index,
               tmp.item_header_flag   AS ifb_header_flag,
               tmp.item_subtotal_flag AS ifb_subtotal_flag,
               tmp.tech_update_date
          FROM src.tmp_balance_item tmp
               JOIN
               tbl_file file
                   ON file.file_name = tmp.file_name
               JOIN
               tbl_emitter emitter
                   ON emitter.emitter_id = file.file_emitter_id
                  AND emitter.emitter_name = tmp.emitter_name
               JOIN
               tbl_item item
                   ON item.item_name = tmp.item_name
               CROSS JOIN
               (SELECT
                       fine_item_id
                  FROM tbl_fine_item
                 WHERE fine_item_code = 'TECH$BLANC') fine_item) src
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_balance
                   WHERE ifb_parent_id IS NOT NULL
                     AND ifb_file_id       = src.ifb_file_id
                     AND ifb_item_id       = src.ifb_item_id
                     AND ifb_fine_item_id  = src.ifb_fine_item_id
                     AND ifb_level         = src.ifb_level
                     AND ifb_index         = src.ifb_index
                     AND ifb_header_flag   = src.ifb_header_flag
                     AND ifb_subtotal_flag = src.ifb_subtotal_flag)
  ON CONFLICT(ifb_file_id, ifb_index)
  DO UPDATE
        SET ifb_parent_id     = excluded.ifb_parent_id,
            ifb_fine_item_id  = excluded.ifb_fine_item_id,
            ifb_level         = excluded.ifb_level,
            ifb_index         = excluded.ifb_index,
            ifb_header_flag   = excluded.ifb_header_flag,
            ifb_subtotal_flag = excluded.ifb_subtotal_flag,
            tech_update_date  = excluded.tech_update_date;
