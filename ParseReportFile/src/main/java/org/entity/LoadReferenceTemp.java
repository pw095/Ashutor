package org.entity;

import org.database.WriteDatabase;
import org.excel.ReadExcelReference;


import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static org.database.Query.getQuery;

public class LoadReferenceTemp extends ReadExcelReference implements WriteDatabase {

    static ResourceBundle rb;
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String insertPath;

    private static final String fieldFile = "tmp_field.sql";
    private static final String emitterFile = "tmp_emitter.sql";
    private static final String auditorFile = "tmp_auditor.sql";
    private static final String reportPeriodFile = "tmp_report_period.sql";

    static {
        rb = ResourceBundle.getBundle("application");
        insertPath = Paths.get(rb.getString("sql_temp_directory"), "insert").toString();
    }

    @Override
    protected void clearTempTableFieldSet(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, fieldFile);

    }

    @Override
    protected void tempFieldSet(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(insertPath, fieldFile).toString());

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
            for (String field : this.getFieldSet()) {
                preparedStatement.setString(1, field);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void clearTempTableEmitterSet(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, emitterFile);

    }

    @Override
    protected void tempEmitterSet(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(insertPath, emitterFile).toString());

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
            for (Emitter emitter : this.getEmitterSet()) {
                preparedStatement.setString(1, emitter.getEmitterName());
                preparedStatement.setString(2, emitter.getEmitterFieldName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected void clearTempTableAuditorSet(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, auditorFile);

    }

    @Override
    protected void tempAuditorSet(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(insertPath, auditorFile).toString());

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
            for (String auditor : this.getAuditorSet()) {
                preparedStatement.setString(1, auditor);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected void clearTempTableReportPeriodSet(Object object) {

        Connection connection = checkConnection(object);
        clearTemporaryTable(connection, reportPeriodFile);

    }

    @Override
    protected void tempReportPeriodSet(Object object) {

        Connection connection = checkConnection(object);
        String queryText = getQuery(Paths.get(insertPath, reportPeriodFile).toString());

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryText)) {
            for (ReportPeriod reportPeriod : this.getReportPeriodSet()) {
                preparedStatement.setString(1, reportPeriod.getReportPeriodCode());
                preparedStatement.setString(2, reportPeriod.getReportPeriodName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void executeAuxiliary(Connection connection) {}

    @Override
    public void writeDestination(Connection connection) {

        clearTempTableFieldSet(connection);
        tempFieldSet(connection);

        clearTempTableEmitterSet(connection);
        tempEmitterSet(connection);

        clearTempTableAuditorSet(connection);
        tempAuditorSet(connection);

        clearTempTableReportPeriodSet(connection);
        tempReportPeriodSet(connection);

    }

    public LoadReferenceTemp() {

        super(rb.getString("file_reference"));
        writeDestination(rb.getString("url_temp"));

    }

}
