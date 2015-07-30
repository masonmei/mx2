// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import java.util.List;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.TransactionData;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.TransactionListener;
import com.newrelic.agent.service.AbstractService;

public class SqlTraceServiceImpl extends AbstractService implements SqlTraceService, TransactionListener, HarvestListener
{
    private static final SqlTracerListener NOP_SQL_TRACER_LISTENER;
    private final ConcurrentMap<String, SqlTracerAggregator> sqlTracerAggregators;
    private final SqlTracerAggregator defaultSqlTracerAggregator;
    private final String defaultAppName;
    
    public SqlTraceServiceImpl() {
        super(SqlTraceService.class.getSimpleName());
        this.sqlTracerAggregators = new ConcurrentHashMap<String, SqlTracerAggregator>();
        this.defaultAppName = ServiceFactory.getConfigService().getDefaultAgentConfig().getApplicationName();
        this.defaultSqlTracerAggregator = this.createSqlTracerAggregator();
    }
    
    private boolean isEnabled(final AgentConfig agentConfig) {
        if (!agentConfig.getSqlTraceConfig().isEnabled()) {
            return false;
        }
        final TransactionTracerConfig ttConfig = agentConfig.getTransactionTracerConfig();
        return !"off".equals(ttConfig.getRecordSql()) && !ttConfig.isLogSql() && ttConfig.isEnabled();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() {
        ServiceFactory.getTransactionService().addTransactionListener(this);
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() {
        ServiceFactory.getTransactionService().removeTransactionListener(this);
        ServiceFactory.getHarvestService().removeHarvestListener(this);
    }
    
    public void dispatcherTransactionFinished(final TransactionData td, final TransactionStats transactionStats) {
        final SqlTracerAggregator aggregator = this.getOrCreateSqlTracerAggregator(td.getApplicationName());
        aggregator.addSqlTracers(td);
    }
    
    public SqlTracerListener getSqlTracerListener(final String appName) {
        final AgentConfig agentConfig = ServiceFactory.getConfigService().getAgentConfig(appName);
        if (this.isEnabled(agentConfig)) {
            final double threshold = agentConfig.getTransactionTracerConfig().getExplainThresholdInMillis();
            return new DefaultSqlTracerListener(threshold);
        }
        return SqlTraceServiceImpl.NOP_SQL_TRACER_LISTENER;
    }
    
    public void afterHarvest(final String appName) {
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        final SqlTracerAggregator aggregator = this.getOrCreateSqlTracerAggregator(appName);
        final List<SqlTrace> sqlTraces = aggregator.getAndClearSqlTracers();
        try {
            ServiceFactory.getRPMService(appName).sendSqlTraceData(sqlTraces);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Error sending sql traces for {0}: {1}", appName, e);
            if (this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().log(Level.FINEST, msg, e);
            }
            else {
                this.getLogger().fine(msg);
            }
        }
    }
    
    private SqlTracerAggregator getOrCreateSqlTracerAggregator(final String appName) {
        SqlTracerAggregator sqlTracerAggregator = this.getSqlTracerAggregator(appName);
        if (sqlTracerAggregator != null) {
            return sqlTracerAggregator;
        }
        sqlTracerAggregator = this.createSqlTracerAggregator();
        final SqlTracerAggregator oldSqlTracerAggregator = this.sqlTracerAggregators.putIfAbsent(appName, sqlTracerAggregator);
        return (oldSqlTracerAggregator == null) ? sqlTracerAggregator : oldSqlTracerAggregator;
    }
    
    private SqlTracerAggregator getSqlTracerAggregator(final String appName) {
        if (appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultSqlTracerAggregator;
        }
        return this.sqlTracerAggregators.get(appName);
    }
    
    private SqlTracerAggregator createSqlTracerAggregator() {
        return new SqlTracerAggregatorImpl();
    }
    
    static {
        NOP_SQL_TRACER_LISTENER = new NopSqlTracerListener();
    }
}
