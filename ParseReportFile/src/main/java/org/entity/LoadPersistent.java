package org.entity;

import org.database.WriteDatabase;

import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;

public final class LoadPersistent implements WriteDatabase {

    private static final String reportTypeFile = "tbl_report_type.sql";
    private static final String fieldFile = "tbl_field.sql";
    private static final String emitterFile = "tbl_emitter.sql";
    private static final String auditorFile = "tbl_auditor.sql";
    private static final String reportPeriodFile = "tbl_report_period.sql";
    private static final String fileFile = "tbl_file.sql";
    private static final String pureItemFile = "tbl_pure_item.sql";
    private static final String itemFile = "tbl_item.sql";
    private static final String fineItemFile = "tbl_fine_item.sql";
    private static final String itemFileBalance1File = "tbl_item_file_balance_1.sql";
    private static final String itemFileBalance2File = "tbl_item_file_balance_2.sql";
    private static final String itemFileBalanceStatSnapFile = "tbl_item_file_balance_statistic_snapshot.sql";
    private static final String itemFileSingle1File = "tbl_item_file_single_1.sql";
    private static final String itemFileSingleStatPeriodFile = "tbl_item_file_single_statistic_period.sql";
    private static final String itemFileDouble1File = "tbl_item_file_double_1.sql";
    private static final String itemFileDoubleStatPeriodFile = "tbl_item_file_double_statistic_period.sql";

    protected void persistReportType(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, reportTypeFile);

    }

    protected void persistField(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fieldFile);

    }

    protected void persistEmitter(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, emitterFile);

    }

    protected void persistAuditor(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, auditorFile);

    }

    protected void persistReportPeriod(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, reportPeriodFile);

    }

    protected void persistFile(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fileFile);

    }

    protected void persistPureItem(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, pureItemFile);

    }

    protected void persistItem(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFile);

    }

    protected void persistFineItem(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fineItemFile);

    }

    protected void persistFileBalance1(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFileBalance1File);

    }

    protected void persistFileBalance2(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFileBalance2File);

    }

    protected void persistFileBalanceStatSnap(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFileBalanceStatSnapFile);

    }

    protected void persistFileSingle1(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFileSingle1File);

    }

    protected void persistFileSingleStatPeriod(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFileSingleStatPeriodFile);

    }

    protected void persistFileDouble1(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFileDouble1File);

    }

    protected void persistFileDoubleStatPeriod(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, itemFileDoubleStatPeriodFile);

    }

    @Override
    public void executeAuxiliary(Connection connection) {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        String auxPathString = Paths.get(rb.getString("sql_persist_directory"), "auxiliary", "attach_database.sql").toString();

        String attachDb = getQuery(auxPathString).replace("$$path", rb.getString("temp_db_path"));
        executeBulkStatements(connection, attachDb);

    }
    @Override
    public void execute(Connection connection) {

//        executeAuxiliary(connection);

        persistReportType(connection);
        persistField(connection);
        persistEmitter(connection);
        persistAuditor(connection);
        persistReportPeriod(connection);
        persistFile(connection);
        persistPureItem(connection);
        persistItem(connection);
        persistFineItem(connection);
        persistFileBalance1(connection);
        persistFileBalance2(connection);
        persistFileBalanceStatSnap(connection);
        persistFileSingle1(connection);
        persistFileSingleStatPeriod(connection);
        persistFileDouble1(connection);
        persistFileDoubleStatPeriod(connection);

    }

    @Override
    public void writeDestination(String path) {
        execute(path);
    }

    public LoadPersistent() {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        writeDestination(rb.getString("url_persist"));
    }

}
