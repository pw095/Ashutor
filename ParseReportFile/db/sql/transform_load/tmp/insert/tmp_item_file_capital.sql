INSERT
  INTO tmp_item_file_capital
  (
    ifc_file_id,
    ifc_horizontal_item_id,
    ifc_horizontal_fine_item_id,
    ifc_horizontal_index,
    ifc_vertical_item_id,
    ifc_vertical_fine_item_id,
    ifc_vertical_index
  )
SELECT
       file.file_id            AS ifc_file_id,
       horizontal_item.item_id AS ifc_horizontal_item_id,
       fine_item.fine_item_id  AS ifc_horizontal_fine_item_id,
       t.item_horizontal_index AS ifc_horizontal_index,
       vertical_item.item_id   AS ifc_vertical_item_id,
       fine_item.fine_item_id  AS ifc_vertical_fine_item_id,
       t.item_vertical_index   AS ifc_vertical_index
  FROM (SELECT
               DISTINCT
               emitter_name,
               file_name,
               item_horizontal_index,
               item_horizontal_name,
               item_vertical_index,
               item_vertical_name
          FROM tmp_item_capital) t
       JOIN
       tbl_emitter emit
           ON emit.emitter_name = t.emitter_name
       JOIN
       tbl_file file
           ON file.file_name = t.file_name
          AND file.file_emitter_id = emit.emitter_id
       JOIN
       tbl_item horizontal_item
           ON horizontal_item.item_name = t.item_horizontal_name
       JOIN
       tbl_item vertical_item
           ON vertical_item.item_name = t.item_vertical_name
       CROSS JOIN
       tbl_fine_item fine_item
 WHERE fine_item.fine_item_code = 'TECH$BLANC'
