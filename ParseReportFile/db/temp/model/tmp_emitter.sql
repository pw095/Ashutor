CREATE TABLE tmp_emitter
  (
    emitter_name       TEXT NOT NULL,
    emitter_field_name TEXT NOT NULL,
    UNIQUE(emitter_name)
  )
