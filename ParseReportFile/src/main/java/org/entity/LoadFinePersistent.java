package org.entity;

import org.database.WriteDatabase;

import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;

public final class LoadFinePersistent implements WriteDatabase {

    private static final String fineItemFile = "tbl_fine_item_3.sql";
    private static final String fineItemFileBalance = "tbl_item_file_balance_3.sql";
    private static final String fineItemFileSingle = "tbl_item_file_single_3.sql";
    private static final String fineItemFileDouble = "tbl_item_file_double_3.sql";

    protected void persistFineItem(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fineItemFile);

    }

    protected void persistFineItemBalance(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fineItemFileBalance);

    }

    protected void persistFineItemSingle(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fineItemFileSingle);

    }

    protected void persistFineItemDouble(Object object) {

        Connection connection = checkConnection(object);
        loadPersistentTable(connection, fineItemFileDouble);

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

        persistFineItem(connection);
        persistFineItemBalance(connection);
        persistFineItemSingle(connection);
        persistFineItemDouble(connection);

    }

    @Override
    public void writeDestination(String path) {
        execute(path);
    }

    public LoadFinePersistent() {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        writeDestination(rb.getString("url_persist"));
    }

}
