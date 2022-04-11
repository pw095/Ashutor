CREATE TABLE tbl_file
  (
    file_id               INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    file_name             TEXT                              NOT NULL,
    file_emitter_id       INTEGER                           NOT NULL,
    file_report_period_id INTEGER                           NOT NULL,
    file_auditor_id       INTEGER                           NOT NULL,
    file_date             TEXT                              NOT NULL,
    file_publish_date     TEXT                              NOT NULL,
    file_currency         TEXT                              NOT NULL,
    file_factor           TEXT                              NOT NULL,
    tech_update_date      TEXT                              NOT NULL,
    UNIQUE(file_name, file_emitter_id),
    UNIQUE(file_date, file_emitter_id),
    FOREIGN KEY(file_emitter_id)       REFERENCES tbl_emitter(emitter_id)             ON DELETE CASCADE,
    FOREIGN KEY(file_report_period_id) REFERENCES tbl_report_period(report_period_id) ON DELETE CASCADE,
    FOREIGN KEY(file_auditor_id)       REFERENCES tbl_auditor(auditor_id)             ON DELETE CASCADE
  );
