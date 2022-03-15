UPDATE tbl_item_file_cf AS dest
   SET ifcf_fine_item_id = (SELECT
                                   fine_item.fine_item_id
                              FROM tmp_fine_item_file_cf_match tmp
                                   JOIN
                                   tbl_emitter emit
                                       ON emit.emitter_name = tmp.emitter_name
                                   JOIN
                                   tbl_file file
                                       ON file.file_emitter_id = emit.emitter_id
                                      AND file.file_date = tmp.file_date
                                   JOIN
                                   tbl_fine_item fine_item
                                       ON fine_item.fine_item_code = tmp.fine_item_code
                             WHERE tmp.ifcf_id = dest.ifcf_id)
 WHERE EXISTS(SELECT
                     NULL
                FROM tmp_fine_item_file_cf_match tmp
                     JOIN
                     tbl_emitter emit
                         ON emit.emitter_name = tmp.emitter_name
                     JOIN
                     tbl_file file
                         ON file.file_emitter_id = emit.emitter_id
                        AND file.file_date = tmp.file_date
                     JOIN
                     tbl_fine_item fine_item
                         ON fine_item.fine_item_code = tmp.fine_item_code
               WHERE tmp.ifcf_id = dest.ifcf_id
                 AND fine_item.fine_item_id != dest.ifcf_fine_item_id)
