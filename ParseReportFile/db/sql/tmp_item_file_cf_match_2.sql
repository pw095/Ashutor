INSERT OR IGNORE
  INTO tmp_item_file_cf_match
  (
    master_ifcf_id,
    depend_ifcf_id
  )
SELECT
       master.ifcf_id AS master_ifcf_id,
       depend.ifcf_id AS depend_ifcf_id
  FROM src.stg_stat_hierarhy hier
       JOIN
       tbl_emitter emit
           ON emit.emitter_name = hier.emitent
       JOIN
       tbl_file master_file
           ON master_file.file_emitter_id = emit.emitter_id
          AND master_file.file_name = hier.master_file
       JOIN
       tbl_item_file_cf master
           ON master.ifcf_number = hier.master_number
          AND master.ifcf_file_id = master_file.file_id
       JOIN
       tbl_file depend_file
           ON depend_file.file_emitter_id = emit.emitter_id
          AND depend_file.file_name = hier.depend_file
       JOIN
       tbl_item_file_cf depend
           ON depend.ifcf_number = hier.depend_number
          AND depend.ifcf_file_id = depend_file.file_id
 WHERE hier.sheet = 'CASH_FLOW'
