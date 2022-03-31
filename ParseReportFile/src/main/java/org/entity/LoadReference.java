package org.entity;

import org.database.WriteDatabase;

import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;

public final class LoadReference implements WriteDatabase {

    private static final String fieldFile = "tbl_field.sql";
    private static final String emitterFile = "tbl_emitter.sql";
    private static final String auditorFile = "tbl_auditor.sql";
    private static final String reportPeriodFile = "tbl_report_period.sql";


    protected void persistFieldSet(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fieldFile);

    }


    protected void persistEmitterSet(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, emitterFile);

    }


    protected void persistAuditorSet(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, auditorFile);

    }


    protected void persistReportPeriodSet(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, reportPeriodFile);

    }

    @Override
    public void executeAuxiliary(Connection connection) {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        String auxPathString = Paths.get(rb.getString("sql_persist_directory"), "auxiliary", "attach_database.sql").toString();

        String attachDb = getQuery(auxPathString).replace("$$path", rb.getString("temp_db_path"));
        executeBulkStatements(connection, attachDb);

    }
    @Override
    public void writeDestination(Connection connection) {

        persistFieldSet(connection);
        persistEmitterSet(connection);
        persistAuditorSet(connection);
        persistReportPeriodSet(connection);

    }

    public LoadReference() {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        writeDestination(rb.getString("url_persist"));
    }

}
