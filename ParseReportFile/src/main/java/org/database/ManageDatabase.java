package org.database;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public interface ManageDatabase {


    default Connection checkConnection(Object object) {

        Connection connection;

        if (object instanceof Connection) {
            connection = (Connection) object;
        } else {
            throw new RuntimeException("Invalid object type!");
        }

        return connection;
    }

    default void execute(String destinationPath) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(destinationPath);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            executeAuxiliary(connection);
            execute(connection);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void executeAuxiliary(Connection connection);
    void execute(Connection connection);
}
