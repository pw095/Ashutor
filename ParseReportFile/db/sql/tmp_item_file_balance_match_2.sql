INSERT OR IGNORE
  INTO tmp_item_file_balance_match
  (
    master_ifb_id,
    depend_ifb_id
  )
SELECT
       master.ifb_id AS master_ifb_id,
       depend.ifb_id AS depend_ifb_id
  FROM src.raw_stat_hierarhy hier
       JOIN
       tbl_emitter emit
           ON emit.emitter_name = hier.emitent
       JOIN
       tbl_file master_file
           ON master_file.file_emitter_id = emit.emitter_id
          AND master_file.file_name = hier.master_file || '.xlsx'
       JOIN
       tbl_item_file_balance master
           ON master.ifb_number = hier.master_number
          AND master.ifb_file_id = master_file.file_id
       JOIN
       tbl_file depend_file
           ON depend_file.file_emitter_id = emit.emitter_id
          AND depend_file.file_name = hier.depend_file || '.xlsx'
       JOIN
       tbl_item_file_balance depend
           ON depend.ifb_number = hier.depend_number
          AND depend.ifb_file_id = depend_file.file_id
 WHERE hier.sheet = 'BALANCE'
