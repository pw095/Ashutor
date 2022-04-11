UPDATE tbl_item_file_balance AS dest
   SET ifb_parent_id = (SELECT
                               ifb_parent.ifb_id
                          FROM src.tmp_balance_item tmp
                               JOIN
                               tbl_emitter emitter
                                   ON emitter.emitter_name = tmp.emitter_name
                               JOIN
                               tbl_file file
                                   ON file.file_name = tmp.file_name
                                  AND file.file_emitter_id = emitter.emitter_id
                               JOIN
                               tbl_item_file_balance ifb_parent
                                   ON ifb_parent.ifb_file_id = file.file_id
                                  AND ifb_parent.ifb_index = tmp.parent_item_index
                         WHERE file.file_id = dest.ifb_file_id
                           AND tmp.item_index = dest.ifb_index)
 WHERE EXISTS(SELECT
                     NULL
                FROM src.tmp_balance_item tmp
                     JOIN
                     tbl_emitter emitter
                         ON emitter.emitter_name = tmp.emitter_name
                     JOIN
                     tbl_file file
                         ON file.file_name = tmp.file_name
                        AND file.file_emitter_id = emitter.emitter_id
               WHERE file.file_id = dest.ifb_file_id
                 AND tmp.item_index = dest.ifb_index)
