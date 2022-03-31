INSERT
  INTO tbl_auditor
  (
    auditor_name
  )
SELECT
       auditor_name
  FROM src.tmp_auditor tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_auditor
                   WHERE auditor_name = tmp.auditor_name)
