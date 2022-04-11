INSERT
  INTO tbl_field
  (
    field_name,
    tech_update_date
  )
SELECT
       field_name,
       tech_update_date
  FROM src.tmp_ref_info tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_field
                   WHERE field_name = tmp.field_name)
