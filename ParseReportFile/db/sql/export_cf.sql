WITH
     w_match AS
     (
       SELECT
              master_ifcf_id,
              COUNT(*)  AS match_cnt
         FROM tbl_item_file_cf_match
        GROUP BY
                 master_ifcf_id
     ),
     w_emit_file AS
     (
       SELECT
              file_emitter_id,
              COUNT(*)        AS file_cnt
         FROM tbl_file
        GROUP BY
                 file_emitter_id
     )
SELECT
       emitent,
       'PL'             AS sheet,
       statement_number,
       file_name,
       file_cnt,
       match_cnt,
       statement,
       NULL             AS statement_level
  FROM (SELECT
               emit.emitter_name              AS emitent,
               ifcf.ifcf_number               AS statement_number,
               file.file_name,
               emit_file.file_cnt,
               1 + IFNULL(match.match_cnt, 0) AS match_cnt,
               item.item_name                 AS statement
          FROM tbl_item_file_cf ifcf
               JOIN
               tbl_file file
                   ON file.file_id = ifcf.ifcf_file_id
               JOIN
               tbl_emitter emit
                   ON emit.emitter_id = file.file_emitter_id
               JOIN
               tbl_item item
                   ON item.item_id = ifcf.ifcf_item_id
               LEFT JOIN
               w_match match
                   ON match.master_ifcf_id = ifcf.ifcf_id
               JOIN
               w_emit_file emit_file
                   ON emit_file.file_emitter_id = file.file_emitter_id
         WHERE NOT EXISTS(SELECT
                                 NULL
                            FROM tbl_item_file_cf_match mtch
                           WHERE mtch.depend_ifcf_id = ifcf.ifcf_id))
 WHERE file_cnt != match_cnt
