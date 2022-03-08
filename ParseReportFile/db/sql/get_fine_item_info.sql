WITH RECURSIVE
    w_pre AS
    (
      SELECT
             ifb_id,
             ifb_parent_id,
             ifb.ifb_fine_item_id,
             ifb_index,
             pure_item.pure_item_name,
             NULL                     AS hier_path,
             ifb.ifb_subtotal_flag
        FROM tbl_item_file_balance ifb
             JOIN
             tbl_item item
                 ON item.item_id = ifb.ifb_item_id
             JOIN
             tbl_pure_item pure_item
                 ON pure_item.pure_item_id = item.pure_item_id
             JOIN
             tbl_file file
                 ON file.file_id = ifb.ifb_file_id
    ),
    w_hier(ifb_id, ifb_parent_id, fine_item_id, ifb_index, level, hier_pure_item_path, ifb_subtotal_flag) AS
    (
      SELECT
             ifb_id,
             ifb_parent_id,
             ifb_fine_item_id,
             ifb_index,
             0                 AS level,
             pure_item_name    AS hier_pure_item_path,
             ifb_subtotal_flag
        FROM w_pre
       WHERE ifb_parent_id IS NULL
       UNION ALL
      SELECT
             pre.ifb_id,
             pre.ifb_parent_id,
             pre.ifb_fine_item_id,
             pre.ifb_index,
             hier.level + 1                                          AS level,
             hier.hier_pure_item_path || ' > ' || pre.pure_item_name AS hier_pure_item_path,
             pre.ifb_subtotal_flag
        FROM w_pre pre
             JOIN
             w_hier hier
                 ON hier.ifb_id = pre.ifb_parent_id
    ),
    w_ord AS
    (
        SELECT
               fine_item_id,
               ROW_NUMBER() OVER (ORDER BY min_ifb_index, min_ifb_subtotal_flag, fine_item_id) AS rn
          FROM (SELECT
                       fine_item_id,
                       MIN(ifb_index)         AS min_ifb_index,
                       MIN(ifb_subtotal_flag) AS min_ifb_subtotal_flag
                  FROM w_hier
                 GROUP BY
                          fine_item_id)
    ),
    w_hier_agg AS
    (
        SELECT
               fine_item_id,
               MIN(hier_pure_item_path) AS hier_pure_item_path
          FROM w_hier
         GROUP BY
                  fine_item_id
    )
SELECT
       fine_item.fine_item_code,
       fine_item.fine_item_name,
       hier.hier_pure_item_path
  FROM w_ord ord
       LEFT JOIN
       tbl_fine_item fine_item
           ON fine_item.fine_item_id = ord.fine_item_id
          AND fine_item.fine_item_code != 'TECH$BLANC'
       JOIN
       w_hier_agg hier
           ON hier.fine_item_id = ord.fine_item_id
 ORDER BY ord.rn
