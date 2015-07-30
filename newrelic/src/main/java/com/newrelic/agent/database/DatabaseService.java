// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.database;

import java.sql.Connection;
import java.util.logging.Level;
import com.newrelic.agent.instrumentation.pointcuts.database.DatabaseUtils;
import com.newrelic.agent.instrumentation.pointcuts.database.ConnectionFactory;
import com.newrelic.agent.instrumentation.pointcuts.database.ExplainPlanExecutor;
import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.service.AbstractService;

public class DatabaseService extends AbstractService implements AgentConfigListener
{
    private static final SqlObfuscator DEFAULT_SQL_OBFUSCATOR;
    private final ConcurrentMap<String, SqlObfuscator> sqlObfuscators;
    private final AtomicReference<SqlObfuscator> defaultSqlObfuscator;
    private final String defaultAppName;
    private final DatabaseStatementParser databaseStatementParser;
    
    public DatabaseService() {
        super(DatabaseService.class.getSimpleName());
        this.sqlObfuscators = new ConcurrentHashMap<String, SqlObfuscator>();
        this.defaultSqlObfuscator = new AtomicReference<SqlObfuscator>();
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.defaultAppName = config.getApplicationName();
        this.databaseStatementParser = new DefaultDatabaseStatementParser(config);
    }
    
    protected void doStart() {
        ServiceFactory.getConfigService().addIAgentConfigListener(this);
    }
    
    protected void doStop() {
        ServiceFactory.getConfigService().removeIAgentConfigListener(this);
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public SqlObfuscator getDefaultSqlObfuscator() {
        return DatabaseService.DEFAULT_SQL_OBFUSCATOR;
    }
    
    public SqlObfuscator getSqlObfuscator(final String appName) {
        final SqlObfuscator sqlObfuscator = this.findSqlObfuscator(appName);
        if (sqlObfuscator != null) {
            return sqlObfuscator;
        }
        return this.createSqlObfuscator(appName);
    }
    
    private SqlObfuscator findSqlObfuscator(final String appName) {
        if (appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultSqlObfuscator.get();
        }
        return this.sqlObfuscators.get(appName);
    }
    
    private SqlObfuscator createSqlObfuscator(final String appName) {
        final TransactionTracerConfig ttConfig = ServiceFactory.getConfigService().getTransactionTracerConfig(appName);
        final SqlObfuscator sqlObfuscator = this.createSqlObfuscator(ttConfig);
        if (appName == null || appName.equals(this.defaultAppName)) {
            if (this.defaultSqlObfuscator.getAndSet(sqlObfuscator) == null) {
                this.logConfig(appName, ttConfig);
            }
        }
        else if (this.sqlObfuscators.put(appName, sqlObfuscator) == null) {
            this.logConfig(appName, ttConfig);
        }
        return sqlObfuscator;
    }
    
    private SqlObfuscator createSqlObfuscator(final TransactionTracerConfig ttConfig) {
        if (!ttConfig.isEnabled()) {
            return SqlObfuscator.getNoSqlObfuscator();
        }
        final String recordSql = ttConfig.getRecordSql();
        if ("off".equals(recordSql)) {
            return SqlObfuscator.getNoSqlObfuscator();
        }
        if ("raw".equals(recordSql)) {
            return SqlObfuscator.getNoObfuscationSqlObfuscator();
        }
        return SqlObfuscator.getDefaultSqlObfuscator();
    }
    
    private void logConfig(final String appName, final TransactionTracerConfig ttConfig) {
        if (ttConfig.isLogSql()) {
            final String msg = MessageFormat.format("Agent is configured to log {0} SQL for {1}", ttConfig.getRecordSql(), appName);
            Agent.LOG.fine(msg);
        }
        else {
            final String msg = MessageFormat.format("Agent is configured to send {0} SQL to New Relic for {1}", ttConfig.getRecordSql(), appName);
            Agent.LOG.fine(msg);
        }
        if (!this.isValidRecordSql(ttConfig.getRecordSql())) {
            final String msg = MessageFormat.format("Unknown value \"{0}\" for record_sql", ttConfig.getRecordSql(), appName);
            Agent.LOG.warning(msg);
        }
    }
    
    private boolean isValidRecordSql(final String recordSql) {
        return "raw".equals(recordSql) || "off".equals(recordSql) || "obfuscated".equals(recordSql);
    }
    
    public void configChanged(final String appName, final AgentConfig agentConfig) {
        Agent.LOG.fine(MessageFormat.format("Database service received configuration change notification for {0}", appName));
        if (appName == null || appName.equals(this.defaultAppName)) {
            this.defaultSqlObfuscator.set(null);
        }
        else {
            this.sqlObfuscators.remove(appName);
        }
    }
    
    public void runExplainPlan(final SqlStatementTracer sqlTracer) {
        final ExplainPlanExecutor explainExecutor = sqlTracer.getExplainPlanExecutor();
        final ConnectionFactory connectionFactory = sqlTracer.getConnectionFactory();
        if (sqlTracer.hasExplainPlan() || explainExecutor == null || connectionFactory == null) {
            return;
        }
        this.runExplainPlan(explainExecutor, connectionFactory);
    }
    
    private void runExplainPlan(final ExplainPlanExecutor explainExecutor, final ConnectionFactory connectionFactory) {
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            final DatabaseVendor vendor = DatabaseUtils.getDatabaseVendor(connection);
            explainExecutor.runExplainPlan(this, connection, vendor);
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("An error occurred executing an explain plan: {0}", t);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg, t);
            }
            else {
                Agent.LOG.fine(msg);
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Exception e) {
                    Agent.LOG.log(Level.FINER, "Unable to close connection", e);
                }
            }
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Exception e2) {
                    Agent.LOG.log(Level.FINER, "Unable to close connection", e2);
                }
            }
        }
    }
    
    public DatabaseStatementParser getDatabaseStatementParser() {
        return this.databaseStatementParser;
    }
    
    static {
        DEFAULT_SQL_OBFUSCATOR = SqlObfuscator.getDefaultSqlObfuscator();
    }
}
