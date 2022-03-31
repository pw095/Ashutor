INSERT
  INTO tbl_field
  (
    field_name
  )
SELECT
       field_name
  FROM src.tmp_field tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_field
                   WHERE field_name = tmp.field_name)
