// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.database.DatabaseStatementParser;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.agent.BoundTransactionApiImpl;
import com.newrelic.agent.bridge.datastore.DatastoreMetrics;
import com.newrelic.agent.stats.TransactionStats;
import java.util.Arrays;
import com.newrelic.agent.database.RecordSql;
import java.sql.SQLException;
import com.newrelic.agent.util.Strings;
import java.sql.Connection;
import java.util.logging.Level;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.database.DatabaseVendor;
import com.newrelic.agent.database.ParsedDatabaseStatement;
import com.newrelic.agent.tracers.ISqlStatementTracer;
import com.newrelic.agent.tracers.DatabaseTracer;
import com.newrelic.agent.tracers.DefaultTracer;

public class SqlStatementTracer extends DefaultTracer implements DatabaseTracer, ISqlStatementTracer, Comparable<SqlStatementTracer>
{
    public static final String EXPLAIN_PLAN_PARAMETER_NAME = "explanation";
    public static final String EXPLAIN_PLAN_FORMAT_PARAMETER_NAME = "explanation_format";
    public static final String DATABASE_VENDOR_PARAMETER_NAME = "database_vendor";
    public static final String SQL_PARAMETER_NAME = "sql";
    public static final String SQL_OBFUSCATED_PARAMETER_NAME = "sql_obfuscated";
    private ParsedDatabaseStatement parsedStatement;
    private final StatementData statementData;
    private ExplainPlanExecutor explainPlanExecutor;
    private ConnectionFactory connectionFactory;
    private Object sqlObject;
    private DatabaseVendor databaseVendor;
    
    public SqlStatementTracer(final Transaction transaction, final ClassMethodSignature sig, final Object statementObject, final StatementData statementData) {
        super(transaction, sig, statementObject, SqlStatementTracer.NULL_METRIC_NAME_FORMATTER, 38);
        this.statementData = statementData;
        if (Agent.isDebugEnabled() && statementData == null) {
            Agent.LOG.finer("No sql for sql statement " + statementObject);
        }
    }
    
    protected void doFinish(final Throwable throwable) {
        this.setDatabaseVendor();
        final Object sql = this.getSqlObject();
        if (sql != null) {
            this.getTransaction().getIntrinsicAttributes().put("sql", sql);
        }
    }
    
    protected Object getSqlObject() {
        if (this.sqlObject != null) {
            return this.sqlObject;
        }
        return (this.statementData == null) ? null : this.statementData.getSql();
    }
    
    public Object getSql() {
        return this.getSqlObject();
    }
    
    protected void doFinish(final int opcode, final Object returnValue) {
        super.doFinish(opcode, returnValue);
        this.getTransaction().getSqlTracerListener().noticeSqlTracer(this);
        if (this.statementData != null) {
            if (this.isTransactionSegment() && this.captureSql()) {
                this.sqlObject = this.getSqlObject();
            }
            this.parsedStatement = this.statementData.getParsedStatement(returnValue, this.getTransaction().getRPMService().getConnectionTimestamp());
            this.setDatabaseVendor();
            this.parsedStatement = new ParsedDatabaseStatement(this.databaseVendor, this.parsedStatement.getModel(), this.parsedStatement.getOperation(), this.parsedStatement.recordMetric());
        }
        if (this.isTransactionSegment() && this.statementData != null && this.statementData.getSql() != null) {
            final TransactionTracerConfig transactionTracerConfig = this.getTransaction().getTransactionTracerConfig();
            final double explainThresholdInNanos = transactionTracerConfig.getExplainThresholdInNanos();
            if (transactionTracerConfig.isExplainEnabled()) {
                this.captureExplain(this.parsedStatement, explainThresholdInNanos, transactionTracerConfig);
            }
            else if (Agent.isDebugEnabled()) {
                final String msg = MessageFormat.format("Statement exceeded threshold?: {0}", this.getDuration() > explainThresholdInNanos);
                Agent.LOG.finer(msg);
            }
        }
    }
    
    protected boolean shouldStoreStackTrace() {
        return super.shouldStoreStackTrace() && this.statementData != null && this.statementData.getSql() != null;
    }
    
    private void setDatabaseVendor() {
        try {
            final Connection connection = this.statementData.getStatement().getConnection();
            this.databaseVendor = DatabaseUtils.getDatabaseVendor(connection);
        }
        catch (Throwable e) {
            Agent.LOG.log(Level.FINEST, "Error getting database information", e);
        }
        finally {
            if (this.databaseVendor == null) {
                this.databaseVendor = DatabaseVendor.UNKNOWN;
            }
        }
    }
    
    private void captureExplain(final ParsedDatabaseStatement parsedStatement, final double explainThresholdInNanos, final TransactionTracerConfig transactionTracerConfig) {
        if (this.getDuration() > explainThresholdInNanos && "select".equals(parsedStatement.getOperation())) {
            if (this.getTransaction().getTransactionCounts().getExplainPlanCount() >= transactionTracerConfig.getMaxExplainPlans()) {
                return;
            }
            final String sql = this.statementData.getSql();
            if (Strings.isEmpty(sql)) {
                this.setExplainPlan("Unable to run the explain plan because we have no sql");
                return;
            }
            try {
                final Connection connection = this.statementData.getStatement().getConnection();
                if (connection == null) {
                    this.setExplainPlan("Unable to run the explain plan because the statement returned a null connection");
                    if (Agent.LOG.isLoggable(Level.FINER)) {
                        Agent.LOG.log(Level.FINER, "Unable to run an explain plan because the Statement.getConnection() returned null : " + this.statementData.getStatement().getClass().getName());
                    }
                    return;
                }
                if (!this.databaseVendor.isExplainPlanSupported()) {
                    this.setExplainPlan("Unable to run explain plans for " + this.databaseVendor.getName() + " databases");
                    return;
                }
                this.connectionFactory = SqlDriverPointCut.getConnectionFactory(this.getTransaction(), connection);
                if (this.connectionFactory != null) {
                    this.explainPlanExecutor = this.createExplainPlanExecutor(sql);
                    if (this.explainPlanExecutor != null) {
                        if (Agent.LOG.isLoggable(Level.FINEST)) {
                            Agent.LOG.finest("Capturing information for explain plan");
                        }
                        this.getTransaction().getTransactionCounts().incrementExplainPlanCountAndLogIfReachedMax(transactionTracerConfig.getMaxExplainPlans());
                    }
                }
                else {
                    this.setExplainPlan("Unable to create a connection to run the explain plan");
                    if (Agent.isDebugEnabled()) {
                        final String msg = MessageFormat.format("Unable to run explain plan because no connection factory ({0}) was found for connection {1}, statement {2}", this.getTransaction().getConnectionCache().getConnectionFactoryCacheSize(), connection.getClass().getName(), this.statementData.getStatement().getClass().getName());
                        Agent.LOG.finer(msg);
                    }
                }
            }
            catch (SQLException e) {
                final String msg = MessageFormat.format("An error occurred running the explain plan: {0}", e);
                this.setExplainPlan(msg);
                Agent.LOG.finer(msg);
            }
            finally {
                if (this.explainPlanExecutor == null) {
                    this.connectionFactory = null;
                }
            }
        }
    }
    
    protected RecordSql getRecordSql() {
        return RecordSql.get(this.getTransaction().getTransactionTracerConfig().getRecordSql());
    }
    
    protected ExplainPlanExecutor createExplainPlanExecutor(final String sql) {
        return new DefaultExplainPlanExecutor(this, sql, this.getRecordSql());
    }
    
    private boolean captureSql() {
        return "off" != this.getTransaction().getTransactionTracerConfig().getRecordSql();
    }
    
    public boolean hasExplainPlan() {
        return this.getAttribute("explanation") != null;
    }
    
    public void setExplainPlan(final Object... explainPlan) {
        this.setAttribute("explanation", Arrays.asList(explainPlan));
        if (this.databaseVendor != DatabaseVendor.UNKNOWN) {
            this.setAttribute("database_vendor", this.databaseVendor.getType());
            this.setAttribute("explanation_format", this.databaseVendor.getExplainPlanFormat());
        }
    }
    
    public boolean isMetricProducer() {
        return this.parsedStatement != null && this.parsedStatement.recordMetric();
    }
    
    protected void recordMetrics(final TransactionStats transactionStats) {
        if (this.isMetricProducer()) {
            final DatastoreMetrics dsMetrics = DatastoreMetrics.getInstance(DatabaseUtils.getDatastoreVendor(this.databaseVendor));
            dsMetrics.collectDatastoreMetrics((com.newrelic.agent.bridge.Transaction)new BoundTransactionApiImpl(this.getTransaction()), (TracedMethod)this, this.parsedStatement.getModel(), this.parsedStatement.getOperation(), (String)null, (String)null);
            if (this.parsedStatement == DatabaseStatementParser.UNPARSEABLE_STATEMENT) {
                dsMetrics.unparsedQuerySupportability();
            }
        }
        super.recordMetrics(transactionStats);
    }
    
    public ExplainPlanExecutor getExplainPlanExecutor() {
        return this.explainPlanExecutor;
    }
    
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
    
    public int compareTo(final SqlStatementTracer otherTracer) {
        final long durationDifference = this.getDuration() - otherTracer.getDuration();
        if (durationDifference < 0L) {
            return -1;
        }
        if (durationDifference > 0L) {
            return 1;
        }
        return 0;
    }
}
