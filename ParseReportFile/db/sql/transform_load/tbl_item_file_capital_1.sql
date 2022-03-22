INSERT OR IGNORE
  INTO tbl_item_file_capital
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
       ifc_file_id,
       ifc_horizontal_item_id,
       ifc_horizontal_fine_item_id,
       ifc_horizontal_index,
       ifc_vertical_item_id,
       ifc_vertical_fine_item_id,
       ifc_vertical_index
  FROM tmp_item_file_capital tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item_file_capital
                   WHERE ifc_file_id = tmp.ifc_file_id
                     AND ifc_horizontal_index = tmp.ifc_horizontal_index
                     AND ifc_vertical_index = tmp.ifc_vertical_index)
