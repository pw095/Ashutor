WITH
     w_ifcf AS
     (
         SELECT
                DISTINCT
                ifcf.ifcf_fine_item_id,
                MAX(item.pure_item_id) OVER (PARTITION BY ifcf.ifcf_fine_item_id
                                                 ORDER BY ifcf.ifcf_index
                                             RANGE BETWEEN UNBOUNDED PRECEDING
                                                       AND UNBOUNDED FOLLOWING) AS pure_item_id,
                MAX(ifcf.ifcf_index)   OVER (PARTITION BY ifcf.ifcf_fine_item_id
                                                 ORDER BY ifcf.ifcf_index
                                             RANGE BETWEEN UNBOUNDED PRECEDING
                                                       AND UNBOUNDED FOLLOWING) AS ifcf_index
           FROM tbl_item_file_cf ifcf
                JOIN
                tbl_item item
                    ON item.item_id = ifcf.ifcf_item_id
     )
SELECT
       fine_item.fine_item_code,
       fine_item.fine_item_name,
       pure_item.pure_item_name AS hier_pure_item_path
  FROM w_ifcf ifcf
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_id = ifcf.pure_item_id
       JOIN
       tbl_fine_item fine_item
           ON fine_item.fine_item_id = ifcf.ifcf_fine_item_id
          AND fine_item.fine_item_code != 'TECH$BLANC'
 ORDER BY
          ifcf.ifcf_index
