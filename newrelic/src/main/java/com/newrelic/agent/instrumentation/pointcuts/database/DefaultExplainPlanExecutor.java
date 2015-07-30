// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.sql.Statement;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.sql.Connection;
import com.newrelic.agent.database.DatabaseService;
import java.sql.SQLException;
import java.util.Collection;
import java.sql.ResultSet;
import com.newrelic.agent.database.DatabaseVendor;
import com.newrelic.agent.database.RecordSql;
import com.newrelic.agent.tracers.ISqlStatementTracer;

public class DefaultExplainPlanExecutor implements ExplainPlanExecutor
{
    private ISqlStatementTracer tracer;
    private final String originalSqlStatement;
    private final RecordSql recordSql;
    
    public DefaultExplainPlanExecutor(final ISqlStatementTracer tracer, final String originalSqlStatement, final RecordSql recordSql) {
        this.originalSqlStatement = originalSqlStatement;
        this.tracer = tracer;
        this.recordSql = recordSql;
    }
    
    private Object[] getExplainPlanFromResultSet(final DatabaseVendor vendor, final ResultSet rs, final RecordSql recordSql) throws SQLException {
        final int columnCount = rs.getMetaData().getColumnCount();
        if (columnCount > 0) {
            final Collection<Collection<Object>> explains = vendor.parseExplainPlanResultSet(columnCount, rs, recordSql);
            return new Object[] { explains };
        }
        return null;
    }
    
    public void runExplainPlan(final DatabaseService databaseService, final Connection connection, final DatabaseVendor vendor) throws SQLException {
        String sql = this.originalSqlStatement;
        try {
            sql = vendor.getExplainPlanSql(sql);
        }
        catch (SQLException e) {
            this.tracer.setExplainPlan(e.getMessage());
            return;
        }
        Agent.LOG.finer("Running explain: " + sql);
        ResultSet resultSet = null;
        Statement statement = null;
        Object[] explainPlan = null;
        try {
            statement = this.createStatement(connection, sql);
            resultSet = this.executeStatement(statement, sql);
            explainPlan = this.getExplainPlanFromResultSet(vendor, resultSet, this.recordSql);
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e2) {
                    Agent.LOG.log(Level.FINER, "Unable to close result set", e2);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e2) {
                    Agent.LOG.log(Level.FINER, "Unable to close statement", e2);
                }
            }
        }
        catch (Exception e3) {
            explainPlan = new Object[] { "An error occurred running explain plan : " + e3.getMessage() };
            Agent.LOG.log(Level.FINER, "explain plan error", e3);
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e2) {
                    Agent.LOG.log(Level.FINER, "Unable to close result set", e2);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e2) {
                    Agent.LOG.log(Level.FINER, "Unable to close statement", e2);
                }
            }
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e2) {
                    Agent.LOG.log(Level.FINER, "Unable to close result set", e2);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e2) {
                    Agent.LOG.log(Level.FINER, "Unable to close statement", e2);
                }
            }
        }
        if (explainPlan != null) {
            this.tracer.setExplainPlan(explainPlan);
        }
    }
    
    protected ResultSet executeStatement(final Statement statement, final String sql) throws SQLException {
        return statement.executeQuery(sql);
    }
    
    protected Statement createStatement(final Connection connection, final String sql) throws SQLException {
        return connection.createStatement();
    }
}
