INSERT OR IGNORE
  INTO tbl_item_file_balance_match
  (
    master_ifb_id,
    depend_ifb_id
  )
WITH
     w_master AS
     (
         SELECT
                DISTINCT
                emitter_name,
                FIRST_VALUE(file_date) OVER (PARTITION BY emitter_name, fine_item_code ORDER BY row_index) AS first_file_date,
                fine_item_code,
                FIRST_VALUE(ifb_id) OVER (PARTITION BY emitter_name, fine_item_code ORDER BY row_index) AS master_ifb_id
           FROM tmp_fine_item_match
     )
SELECT
       master.master_ifb_id,
       tmp.ifb_id           AS depend_ifb_id
  FROM w_master master
       JOIN
       tmp_fine_item_match tmp
          ON tmp.emitter_name = master.emitter_name
         AND tmp.fine_item_code = master.fine_item_code
         AND tmp.ifb_id != master.master_ifb_id
