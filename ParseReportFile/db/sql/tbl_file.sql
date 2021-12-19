INSERT
  INTO tbl_file
  (
    file_name,
    file_emitter_id,
    file_date,
    file_currency,
    file_factor
  )
SELECT
       file_name,
       file_emitter_id,
       file_date,
       file_currency,
       CASE file_factor
            WHEN 'млн' THEN
                1000000
            WHEN 'тыс' THEN
                1000
       END AS file_factor
  FROM (SELECT
               file_name,
               emitter_id                             AS file_emitter_id,
               file_date,
               SUBSTR(pure_statement, space_ind+1)    AS file_currency,
               SUBSTR(pure_statement, 1, space_ind-1) AS file_factor
          FROM (SELECT
                       file_name,
                       emit.emitter_id,
                       SUBSTR(file_name, 1, file_extension_pos-1) || '-12-31' AS file_date,
                       pure_statement,
                       INSTR(pure_statement, ' ') AS space_ind
                  FROM (SELECT
                               DISTINCT
                               emitent,
                               file_name,
                               INSTR(file_name, '.xlsx') AS file_extension_pos,
                               pure_statement
                          FROM src.stg_statement
                         WHERE statement_number = 0) raw
                       JOIN
                       tbl_emitter emit
                           ON emit.emitter_name = raw.emitent))
 WHERE 1 = 1
 ON CONFLICT(file_name, file_emitter_id)
 DO UPDATE
       SET file_date     = excluded.file_date,
           file_currency = excluded.file_currency,
           file_factor   = excluded.file_factor
