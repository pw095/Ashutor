WITH RECURSIVE
    w_pre AS
    (
      SELECT
             file.file_date,
             ifb_id,
             ifb_parent_id,
             fine_item.fine_item_code,
             ifb_index,
             pure_item.pure_item_name,
             NULL                     AS hier_path
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
             LEFT JOIN
             tbl_fine_item fine_item
                 ON fine_item.fine_item_id = ifb.ifb_fine_item_id
                AND fine_item.fine_item_code != 'TECH$BLANC'
       WHERE EXISTS(SELECT
                           NULL
                      FROM tbl_emitter emit
                     WHERE emit.emitter_id = file.file_emitter_id
                       AND emit.emitter_name = :emitter_name)
    ),
    w_hier(file_date, ifb_id, ifb_parent_id, fine_item_code, ifb_index, level, hier_fine_item_path, hier_pure_item_path) AS
    (
      SELECT
             file_date,
             ifb_id,
             ifb_parent_id,
             fine_item_code,
             ifb_index,
             0                AS level,
             fine_item_code,
             pure_item_name   AS hier_pure_item_path
        FROM w_pre
       WHERE ifb_parent_id IS NULL
       UNION ALL
      SELECT
             pre.file_date,
             pre.ifb_id,
             pre.ifb_parent_id,
             pre.fine_item_code,
             pre.ifb_index,
             hier.level + 1                                          AS level,
             pre.fine_item_code,
             hier.hier_pure_item_path || ' > ' || pre.pure_item_name AS hier_pure_item_path
        FROM w_pre pre
             JOIN
             w_hier hier
                 ON hier.ifb_id = pre.ifb_parent_id
    )
SELECT
       MAX(fine_item_code)                             AS fine_item_code,
       MAX(hier_pure_item_path)                        AS hier_pure_item_path,
       MIN(level)                                      AS level,
       MIN(ifb_index)                                  AS ifb_index,
       COUNT(*)                                        AS cnt,
       GROUP_CONCAT(file_date || ': ' || ifb_id, ', ') AS group_cnct
  FROM w_hier
 GROUP BY
          IFNULL(fine_item_code, hier_pure_item_path),
          hier_pure_item_path
 ORDER BY
          ifb_index,
          fine_item_code,
          hier_pure_item_path
