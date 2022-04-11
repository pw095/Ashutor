INSERT
  INTO tbl_item
  (
    item_name,
    pure_item_id,
    tech_update_date
  )
SELECT
       src.item_name,
       pure_item.pure_item_id,
       src.tech_update_date
  FROM (SELECT
               item_name,
               MAX(pure_item_name)             AS pure_item_name,
               MAX(datetime(tech_update_date)) AS tech_update_date
          FROM (SELECT
                       item_name,
                       item_pure_name   AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_balance_item
                 UNION ALL
                SELECT
                       item_name,
                       item_pure_name   AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_single_dimension_item
                 UNION ALL
                SELECT
                       item_horizontal_name      AS item_name,
                       item_horizontal_pure_name AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_double_dimension_item
                 UNION ALL
                SELECT
                       item_vertical_name      AS item_name,
                       item_vertical_pure_name AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_double_dimension_item)
         GROUP BY
                  item_name) src
      JOIN
      tbl_pure_item pure_item
          ON pure_item.pure_item_name = src.pure_item_name
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_item
                   WHERE item_name = src.item_name)
  ON CONFLICT(item_name)
  DO UPDATE
        SET pure_item_id     = excluded.pure_item_id,
            tech_update_date = excluded.tech_update_date
