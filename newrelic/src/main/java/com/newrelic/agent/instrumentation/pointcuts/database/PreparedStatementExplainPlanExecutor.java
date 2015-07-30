// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import com.newrelic.agent.database.RecordSql;
import com.newrelic.agent.tracers.ISqlStatementTracer;

class PreparedStatementExplainPlanExecutor extends DefaultExplainPlanExecutor
{
    private final Object[] sqlParameters;
    
    public PreparedStatementExplainPlanExecutor(final ISqlStatementTracer tracer, final String originalSqlStatement, final Object[] sqlParameters, final RecordSql recordSql) {
        super(tracer, originalSqlStatement, recordSql);
        this.sqlParameters = sqlParameters;
    }
    
    protected Statement createStatement(final Connection connection, final String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
    
    protected ResultSet executeStatement(final Statement statement, final String sql) throws SQLException {
        final PreparedStatement preparedStatement = (PreparedStatement)statement;
        this.setSqlParameters(preparedStatement);
        return preparedStatement.executeQuery();
    }
    
    private void setSqlParameters(final PreparedStatement preparedStatement) throws SQLException {
        if (null == this.sqlParameters) {
            return;
        }
        int length = this.sqlParameters.length;
        try {
            length = Math.min(length, preparedStatement.getMetaData().getColumnCount());
        }
        catch (Throwable t) {}
        try {
            for (int i = 0; i < length; ++i) {
                preparedStatement.setObject(i + 1, this.sqlParameters[i]);
            }
        }
        catch (Throwable t2) {}
    }
}
