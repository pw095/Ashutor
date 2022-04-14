UPDATE tbl_item_file_double AS dest
   SET (ifd_horizontal_fine_item_id, ifd_vertical_fine_item_id, tech_update_date)
                        = (SELECT
                                  h_fine.fine_item_id,
                                  v_fine.fine_item_id,
                                  t.tech_update_date
                             FROM (SELECT
                                          if_id,
                                          MAX(vertical_fine_item_code)   AS vertical_fine_item_code,
                                          MAX(horizontal_fine_item_code) AS horizontal_fine_item_code,
                                          MAX(tech_update_date)          AS tech_update_date
                                     FROM (SELECT
                                                   if_id,
                                                  CASE vertical_report_type_code_id
                                                       WHEN 0 THEN
                                                           NULL
                                                       ELSE
                                                           fine_item_code
                                                  END AS vertical_fine_item_code,
                                                  CASE horizontal_report_type_code_id
                                                       WHEN 0 THEN
                                                           NULL
                                                       ELSE
                                                           fine_item_code
                                                  END AS horizontal_fine_item_code,
                                                  tech_update_date
                                             FROM (SELECT
                                                          if_id,
                                                          fine_item_code,
                                                          report_type_code,
                                                          INSTR(report_type_code, '_VERTICAL')   AS vertical_report_type_code_id,
                                                          INSTR(report_type_code, '_HORIZONTAL') AS horizontal_report_type_code_id,
                                                          tech_update_date
                                                     FROM src.tmp_pure_fine_item_match)
                                            WHERE vertical_report_type_code_id + horizontal_report_type_code_id > 0)
                                    GROUP BY if_id) t
                                  JOIN
                                  tbl_fine_item h_fine
                                      ON h_fine.fine_item_code = t.horizontal_fine_item_code
                                  JOIN
                                  tbl_fine_item v_fine
                                      ON v_fine.fine_item_code = t.vertical_fine_item_code
                                 WHERE t.if_id = dest.ifd_id)
 WHERE EXISTS(SELECT
                     NULL
                FROM (SELECT
                             if_id,
                             MAX(vertical_fine_item_code)   AS vertical_fine_item_code,
                             MAX(horizontal_fine_item_code) AS horizontal_fine_item_code,
                             MAX(tech_update_date)          AS tech_update_date
                        FROM (SELECT
                                      if_id,
                                     CASE vertical_report_type_code_id
                                          WHEN 0 THEN
                                              NULL
                                          ELSE
                                              fine_item_code
                                     END AS vertical_fine_item_code,
                                     CASE horizontal_report_type_code_id
                                          WHEN 0 THEN
                                              NULL
                                          ELSE
                                              fine_item_code
                                     END AS horizontal_fine_item_code,
                                     tech_update_date
                                FROM (SELECT
                                             if_id,
                                             fine_item_code,
                                             report_type_code,
                                             INSTR(report_type_code, '_VERTICAL')   AS vertical_report_type_code_id,
                                             INSTR(report_type_code, '_HORIZONTAL') AS horizontal_report_type_code_id,
                                             tech_update_date
                                        FROM src.tmp_pure_fine_item_match)
                               WHERE vertical_report_type_code_id + horizontal_report_type_code_id > 0)
                       GROUP BY if_id) t
                     JOIN
                     tbl_fine_item h_fine
                         ON h_fine.fine_item_code = t.horizontal_fine_item_code
                     JOIN
                     tbl_fine_item v_fine
                         ON v_fine.fine_item_code = t.vertical_fine_item_code
                    WHERE t.if_id = dest.ifd_id
                      AND (   h_fine.fine_item_id != dest.ifd_horizontal_fine_item_id
                           OR v_fine.fine_item_id != dest.ifd_vertical_fine_item_id))
