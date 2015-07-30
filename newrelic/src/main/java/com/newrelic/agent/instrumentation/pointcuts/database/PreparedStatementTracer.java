// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.deps.org.json.simple.JSONAware;
import com.newrelic.agent.tracers.ISqlStatementTracer;
import com.newrelic.agent.database.RecordSql;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;

class PreparedStatementTracer extends SqlStatementTracer
{
    private Object[] sqlParameters;
    private final PreparedStatementExtension preparedStatement;
    
    public PreparedStatementTracer(final Transaction transaction, final ClassMethodSignature sig, final PreparedStatementExtension preparedStatement, final StatementData statementData) {
        super(transaction, sig, preparedStatement, statementData);
        this.preparedStatement = preparedStatement;
    }
    
    protected void doFinish(final int opcode, final Object returnValue) {
        if ("raw" == this.getTransaction().getTransactionTracerConfig().getRecordSql() || this.getDuration() > this.getTransaction().getTransactionTracerConfig().getExplainThresholdInNanos()) {
            final Object[] parameters = this.preparedStatement._nr_getSqlParameters();
            this.sqlParameters = (Object[])((parameters == null) ? null : new Object[parameters.length]);
            if (parameters != null) {
                System.arraycopy(parameters, 0, this.sqlParameters, 0, parameters.length);
            }
        }
        super.doFinish(opcode, returnValue);
    }
    
    Object[] getSqlParameters() {
        return this.sqlParameters;
    }
    
    protected Object getSqlObject() {
        final String sql = this.preparedStatement._nr_getStatementData().getSql();
        if (!RecordSql.raw.equals(this.getRecordSql())) {
            return sql;
        }
        if (this.getSqlParameters() != null && this.getSqlParameters().length > 0) {
            return new PreparedStatementSql(sql, this.getSqlParameters());
        }
        return sql;
    }
    
    protected ExplainPlanExecutor createExplainPlanExecutor(final String sql) {
        return new PreparedStatementExplainPlanExecutor(this, sql, this.sqlParameters, this.getRecordSql());
    }
    
    private static class PreparedStatementSql implements JSONAware
    {
        private final String sql;
        private final Object[] sqlParameters;
        
        public PreparedStatementSql(final String sql, final Object[] sqlParameters) {
            this.sql = sql;
            this.sqlParameters = sqlParameters;
        }
        
        public String toJSONString() {
            try {
                return AbstractPreparedStatementPointCut.parameterizeSql(this.sql, this.sqlParameters);
            }
            catch (Exception e) {
                return this.sql;
            }
        }
        
        public String toString() {
            return this.toJSONString();
        }
    }
}
