// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.api.agent.Insights;
import com.newrelic.agent.bridge.NoOpMetricAggregator;
import com.newrelic.api.agent.MetricAggregator;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.Config;
import com.newrelic.agent.bridge.NoOpTransaction;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.agent.bridge.NoOpTracedMethod;
import java.util.logging.Level;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.api.agent.Logger;
import com.newrelic.agent.bridge.Agent;

public class AgentImpl implements Agent
{
    private final Logger logger;
    
    public AgentImpl(final Logger logger) {
        this.logger = logger;
    }
    
    public TracedMethod getTracedMethod() {
        this.getLogger().log(Level.FINER, "Unexpected call to Agent.getTracedMethod()", new Object[0]);
        return NoOpTracedMethod.INSTANCE;
    }
    
    public Transaction getTransaction() {
        final com.newrelic.agent.Transaction innerTx = com.newrelic.agent.Transaction.getTransaction();
        if (innerTx != null) {
            return (Transaction)new TransactionApiImpl();
        }
        return NoOpTransaction.INSTANCE;
    }
    
    public Logger getLogger() {
        return this.logger;
    }
    
    public Config getConfig() {
        return (Config)ServiceFactory.getConfigService().getDefaultAgentConfig();
    }
    
    public MetricAggregator getMetricAggregator() {
        try {
            final com.newrelic.agent.Transaction tx = com.newrelic.agent.Transaction.getTransaction();
            if (null != tx && tx.isInProgress()) {
                return tx.getMetricAggregator();
            }
            return ServiceFactory.getStatsService().getMetricAggregator();
        }
        catch (Throwable t) {
            com.newrelic.agent.Agent.LOG.log(Level.FINE, "getMetricAggregator() call failed : {0}", new Object[] { t.getMessage() });
            com.newrelic.agent.Agent.LOG.log(Level.FINEST, t, "getMetricAggregator() call failed", new Object[0]);
            return NoOpMetricAggregator.INSTANCE;
        }
    }
    
    public Insights getInsights() {
        return (Insights)ServiceFactory.getServiceManager().getInsights();
    }
}
