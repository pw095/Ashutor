package org.database;

import org.destination.WriteDestination;
import org.sqlite.SQLiteDataSource;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;


public interface WriteDatabase extends WriteDestination, ManageDatabase {

    default void executeBulkStatementsByPath(Connection connection, String ... statementPaths) {

        Arrays
            .asList(statementPaths)
            .stream()
            .map(Query::getQuery)
            .forEachOrdered(p -> executeBulkStatements(connection,p));


    }

    default void executeBulkStatements(Connection connection, String ... statementsText) {

        for (String statementText : statementsText) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statementText)) {
                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    default void clearTemporaryTable(Connection connection, String statementPath) {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        String pathString = Paths.get(rb.getString("sql_temp_directory"), "delete", statementPath).toString();
        executeBulkStatementsByPath(connection, pathString);

    }

    default void loadPersistentTable(Connection connection, String statementPath) {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        String queryString = getQuery(Paths.get(rb.getString("sql_persist_directory"), "insert", statementPath).toString());
        executeBulkStatements(connection, queryString);

    }

}
