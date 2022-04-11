INSERT
  INTO tbl_report_type
  (
    report_type_code,
    report_type_name,
    tech_update_date
  )
SELECT
       report_type_code,
       report_type_code AS report_type_name,
       tech_update_date
  FROM (SELECT
               'BALANCE'        AS report_type_code,
               tech_update_date
          FROM src.tmp_balance_item
         UNION
        SELECT
               report_type_code,
               tech_update_date
          FROM src.tmp_single_dimension_item
         UNION
        SELECT
               report_type_code,
               tech_update_date
          FROM src.tmp_double_dimension_item) tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_report_type
                   WHERE report_type_code = tmp.report_type_code
                     AND report_type_name = tmp.report_type_code)
  ON CONFLICT(report_type_code)
  DO UPDATE
        SET report_type_name = excluded.report_type_name,
            tech_update_date = excluded.tech_update_date
