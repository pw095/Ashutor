INSERT
  INTO tbl_pure_item
    (
      pure_item_name,
      tech_update_date
    )
SELECT
       pure_item_name,
       tech_update_date
  FROM (SELECT
               pure_item_name,
               MAX(tech_update_date) AS tech_update_date
          FROM (SELECT
                       item_pure_name   AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_balance_item
                 UNION ALL
                SELECT
                       item_pure_name   AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_single_dimension_item
                 UNION ALL
                SELECT
                       item_horizontal_pure_name AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_double_dimension_item
                 UNION ALL
                SELECT
                       item_vertical_pure_name AS pure_item_name,
                       tech_update_date
                  FROM src.tmp_double_dimension_item)
         GROUP BY
                  pure_item_name) src
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_pure_item
                   WHERE pure_item_name = src.pure_item_name);
