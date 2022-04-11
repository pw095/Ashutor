INSERT
  INTO tbl_emitter
  (
    emitter_field_id,
    emitter_name,
    tech_update_date
  )
SELECT
       field.field_id       AS emitter_field_id,
       tmp.emitter_name,
       tmp.tech_update_date
  FROM src.tmp_ref_info tmp
       JOIN
       tbl_field field
           ON field.field_name = tmp.field_name
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_emitter
                   WHERE emitter_field_id = field.field_id
                     AND emitter_name = tmp.emitter_name)
  ON CONFLICT(emitter_name)
  DO UPDATE
        SET emitter_field_id = excluded.emitter_field_id,
            tech_update_date = excluded.tech_update_date
