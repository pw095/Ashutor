WITH
     w_ifc AS
     (
         SELECT
                DISTINCT
                ifc.ifc_vertical_fine_item_id,
                LAST_VALUE(item.pure_item_id)      OVER (PARTITION BY ifc.ifc_vertical_fine_item_id
                                                             ORDER BY ifc.ifc_vertical_index) AS pure_item_id,
                LAST_VALUE(ifc.ifc_vertical_index) OVER (PARTITION BY ifc.ifc_vertical_fine_item_id
                                                             ORDER BY ifc.ifc_vertical_index) AS ifc_index
           FROM tbl_item_file_capital ifc
                JOIN
                tbl_item item
                    ON item.item_id = ifc.ifc_vertical_item_id
     )
SELECT
       fine_item.fine_item_code,
       fine_item.fine_item_name,
       pure_item.pure_item_name AS hier_pure_item_path
  FROM w_ifc ifc
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_id = ifc.pure_item_id
       JOIN
       tbl_fine_item fine_item
           ON fine_item.fine_item_id = ifc.ifc_vertical_fine_item_id
          AND fine_item.fine_item_code != 'TECH$BLANC'
 ORDER BY
          ifc.ifc_index
