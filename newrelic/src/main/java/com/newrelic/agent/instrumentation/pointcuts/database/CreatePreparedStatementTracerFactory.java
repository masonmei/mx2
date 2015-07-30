// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import java.sql.Statement;
import java.util.logging.Level;
import com.newrelic.agent.tracers.DatabaseTracer;
import com.newrelic.agent.tracers.MethodExitTracer;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.AbstractTracerFactory;

public class CreatePreparedStatementTracerFactory extends AbstractTracerFactory
{
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object connection, final Object[] args) {
        final String sql = (args.length > 0) ? ((String)args[0]) : null;
        if (sql != null) {
            return new CreatePreparedStatementTracer(transaction, sig, connection, sql);
        }
        if (Agent.isDebugEnabled()) {
            final String msg = MessageFormat.format("Prepared statement sql was null: {0}", sig);
            Agent.LOG.finest(msg);
        }
        return null;
    }
    
    private class CreatePreparedStatementTracer extends MethodExitTracer implements DatabaseTracer
    {
        private String sql;
        private final boolean initParameters;
        
        public CreatePreparedStatementTracer(final Transaction transaction, final ClassMethodSignature sig, final Object connection, final String sql) {
            super(sig, transaction);
            this.sql = sql;
            this.initParameters = ("raw" == transaction.getTransactionTracerConfig().getRecordSql() || transaction.getTransactionTracerConfig().isExplainEnabled());
        }
        
        protected void doFinish(final int opcode, final Object statement) {
            final boolean isLoggable = Agent.LOG.isLoggable(Level.FINEST);
            if (statement instanceof DelegatingPreparedStatement) {
                if (isLoggable) {
                    final String msg = MessageFormat.format("Skipping delegating prepared statement: {0}", statement.getClass().getName());
                    Agent.LOG.finest(msg);
                }
                return;
            }
            if (!(statement instanceof PreparedStatementExtension)) {
                if (isLoggable) {
                    final String msg = MessageFormat.format("{0} does not implement {1}", statement.getClass().getName(), PreparedStatementExtension.class.getName());
                    Agent.LOG.finest(msg);
                }
                return;
            }
            final PreparedStatementExtension prepStatment = (PreparedStatementExtension)statement;
            if (prepStatment._nr_getStatementData() != null) {
                return;
            }
            final DefaultStatementData statementData = new DefaultStatementData(this.getTransaction().getDatabaseStatementParser(), (Statement)statement, this.sql);
            prepStatment._nr_setStatementData(statementData);
            if (isLoggable) {
                Agent.LOG.finest(MessageFormat.format("Storing SQL: {0} for PreparedStatement: {1}", this.sql, statement.getClass().getName()));
            }
            if (this.initParameters) {
                final Object[] statementParameters = new Object[16];
                prepStatment._nr_setSqlParameters(statementParameters);
            }
            this.sql = null;
        }
    }
    
    @InterfaceMixin(originalClassName = { "com/newrelic/agent/deps/org/apache/commons/dbcp/DelegatingPreparedStatement" })
    public interface DelegatingPreparedStatement
    {
    }
}
