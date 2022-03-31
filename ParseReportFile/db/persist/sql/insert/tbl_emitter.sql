INSERT
  INTO tbl_emitter
  (
    emitter_field_id,
    emitter_name
  )
SELECT
       field.field_id,
       tmp.emitter_name
  FROM src.tmp_emitter tmp
       JOIN
       tbl_field field
           ON field.field_name = tmp.emitter_field_name
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_emitter
                   WHERE emitter_name = tmp.emitter_name)
