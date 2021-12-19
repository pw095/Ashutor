INSERT OR IGNORE
  INTO tmp_item_file_pl_match
  (
    master_ifpl_id,
    depend_ifpl_id
  )
SELECT
       master.ifpl_id AS master_ifpl_id,
       depend.ifpl_id AS depend_ifpl_id
  FROM src.stg_stat_hierarhy hier
       JOIN
       tbl_emitter emit
           ON emit.emitter_name = hier.emitent
       JOIN
       tbl_file master_file
           ON master_file.file_emitter_id = emit.emitter_id
          AND master_file.file_name = hier.master_file
       JOIN
       tbl_item_file_pl master
           ON master.ifpl_number = hier.master_number
          AND master.ifpl_file_id = master_file.file_id
       JOIN
       tbl_file depend_file
           ON depend_file.file_emitter_id = emit.emitter_id
          AND depend_file.file_name = hier.depend_file
       JOIN
       tbl_item_file_pl depend
           ON depend.ifpl_number = hier.depend_number
          AND depend.ifpl_file_id = depend_file.file_id
 WHERE hier.sheet = 'PL'
