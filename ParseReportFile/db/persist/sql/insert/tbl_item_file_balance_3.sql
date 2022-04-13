UPDATE tbl_item_file_balance AS dest
   SET (ifb_fine_item_id, tech_update_date)
                        = (SELECT
                                  fine_item.fine_item_id,
                                  tmp.tech_update_date
                             FROM src.tmp_pure_fine_item_match tmp
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
                            WHERE tmp.if_id = dest.ifb_id
                              AND tmp.report_type_code = 'BALANCE')
 WHERE EXISTS(SELECT
                     NULL
                FROM src.tmp_pure_fine_item_match tmp
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
               WHERE tmp.if_id = dest.ifb_id
                 AND tmp.report_type_code = 'BALANCE'
                 AND fine_item.fine_item_id != dest.ifb_fine_item_id)
