INSERT
  INTO tbl_fine_item
  (
    fine_item_code,
    fine_item_name,
    tech_update_date
  )
SELECT
       fine_item_code,
       fine_item_name,
       tech_update_date
  FROM (SELECT
               fine_item_code,
               NULL             AS fine_item_name,
               tech_update_date
          FROM (SELECT
                       fine_item_code,
                       MAX(tech_update_date) AS tech_update_date
                  FROM src.tmp_pure_fine_item_match
                 WHERE fine_item_code != 'TECH$BLANC'
                 GROUP BY
                         fine_item_code)) src
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_fine_item
                   WHERE fine_item_code = src.fine_item_code
                     AND IFNULL(fine_item_name, '!@#$%^&*') = IFNULL(src.fine_item_name, '!@#$%^&*'))
  ON CONFLICT(fine_item_code)
  DO UPDATE
        SET fine_item_name   = excluded.fine_item_name,
            tech_update_date = excluded.tech_update_date;
