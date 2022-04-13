CREATE TABLE tmp_double_dimension_statistic_period
  (
    emitter_name          TEXT,
    file_name             TEXT,
    report_type_code      TEXT,
    item_horizontal_index INTEGER,
    item_vertical_index   INTEGER,
    report_start_date     TEXT,
    report_end_date       TEXT,
    report_value          REAL,
    tech_update_date      TEXT
  );
