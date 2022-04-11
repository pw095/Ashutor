INSERT
  INTO tbl_auditor
  (
    auditor_name,
    tech_update_date
  )
SELECT
       auditor_name,
       tech_update_date
  FROM src.tmp_ref_info tmp
 WHERE NOT EXISTS(SELECT
                         NULL
                    FROM tbl_auditor
                   WHERE auditor_name = tmp.auditor_name)
