WITH
     w_ifpl AS
     (
         SELECT
                DISTINCT
                ifpl.ifpl_fine_item_id,
                MAX(item.pure_item_id) OVER (PARTITION BY ifpl.ifpl_fine_item_id
                                                 ORDER BY ifpl.ifpl_index
                                             RANGE BETWEEN UNBOUNDED PRECEDING
                                                       AND UNBOUNDED FOLLOWING) AS pure_item_id,
                MAX(ifpl.ifpl_index)   OVER (PARTITION BY ifpl.ifpl_fine_item_id
                                                 ORDER BY ifpl.ifpl_index
                                             RANGE BETWEEN UNBOUNDED PRECEDING
                                                       AND UNBOUNDED FOLLOWING) AS ifpl_index
           FROM tbl_item_file_pl ifpl
                JOIN
                tbl_item item
                    ON item.item_id = ifpl.ifpl_item_id
     )
SELECT
       fine_item.fine_item_code,
       fine_item.fine_item_name,
       pure_item.pure_item_name AS hier_pure_item_path
  FROM w_ifpl ifpl
       JOIN
       tbl_pure_item pure_item
           ON pure_item.pure_item_id = ifpl.pure_item_id
       JOIN
       tbl_fine_item fine_item
           ON fine_item.fine_item_id = ifpl.ifpl_fine_item_id
          AND fine_item.fine_item_code != 'TECH$BLANC'
 ORDER BY
          ifpl.ifpl_index
