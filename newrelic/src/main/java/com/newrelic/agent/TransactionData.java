// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.sql.SqlTracerListener;
import com.newrelic.agent.stats.ApdexPerfZone;
import com.newrelic.agent.transaction.PriorityTransactionName;
import java.util.Map;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.config.AgentConfig;
import java.util.Collection;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.transaction.TransactionTimer;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.api.agent.Insights;

public class TransactionData
{
    private final Transaction tx;
    private final int transactionSize;
    private final long threadId;
    
    public TransactionData(final Transaction transaction, final int transactionSize) {
        this.tx = transaction;
        this.transactionSize = transactionSize;
        this.threadId = Thread.currentThread().getId();
    }
    
    public Insights getInsightsData() {
        return this.tx.getInsightsData();
    }
    
    public Dispatcher getDispatcher() {
        return this.tx.getDispatcher();
    }
    
    public TransactionTimer getTransactionTime() {
        return this.tx.getTransactionTimer();
    }
    
    public Tracer getRootTracer() {
        return this.tx.getRootTracer();
    }
    
    public Collection<Tracer> getTracers() {
        return this.tx.getAllTracers();
    }
    
    public long getWallClockStartTimeMs() {
        return this.tx.getWallClockStartTimeMs();
    }
    
    public long getStartTimeInNanos() {
        return this.tx.getTransactionTimer().getStartTime();
    }
    
    public long getEndTimeInNanos() {
        return this.tx.getTransactionTimer().getEndTime();
    }
    
    public String getRequestUri() {
        return this.getDispatcher().getUri();
    }
    
    public int getResponseStatus() {
        return this.tx.getStatus();
    }
    
    public String getStatusMessage() {
        return this.tx.getStatusMessage();
    }
    
    public final long getThreadId() {
        return this.threadId;
    }
    
    public String getApplicationName() {
        return this.tx.getRPMService().getApplicationName();
    }
    
    public AgentConfig getAgentConfig() {
        return this.tx.getAgentConfig();
    }
    
    public TransactionTracerConfig getTransactionTracerConfig() {
        return (this.getAgentConfig() == null) ? null : this.getAgentConfig().getTransactionTracerConfig();
    }
    
    public Map<String, Object> getInternalParameters() {
        return this.tx.getInternalParameters();
    }
    
    public Map<String, Map<String, String>> getPrefixedAttributes() {
        return this.tx.getPrefixedAgentAttributes();
    }
    
    public Map<String, Object> getUserAttributes() {
        return this.tx.getUserAttributes();
    }
    
    public Map<String, Object> getAgentAttributes() {
        return this.tx.getAgentAttributes();
    }
    
    public Map<String, String> getErrorAttributes() {
        return this.tx.getErrorAttributes();
    }
    
    public Map<String, Object> getIntrinsicAttributes() {
        return this.tx.getIntrinsicAttributes();
    }
    
    public PriorityTransactionName getPriorityTransactionName() {
        return this.tx.getPriorityTransactionName();
    }
    
    public String getBlameMetricName() {
        return this.getPriorityTransactionName().getName();
    }
    
    public String getBlameOrRootMetricName() {
        return (this.getBlameMetricName() == null) ? this.getRootTracer().getMetricName() : this.getBlameMetricName();
    }
    
    public Throwable getThrowable() {
        return this.tx.getReportError();
    }
    
    protected final int getTransactionSize() {
        return this.transactionSize;
    }
    
    public long getDurationInMillis() {
        return this.tx.getTransactionTimer().getResponseTimeInMilliseconds();
    }
    
    public long getDuration() {
        return this.tx.getTransactionTimer().getResponseTime();
    }
    
    public String getGuid() {
        return this.tx.getGuid();
    }
    
    public String getReferrerGuid() {
        return this.tx.getInboundHeaderState().getReferrerGuid();
    }
    
    public String getTripId() {
        return this.tx.getCrossProcessTransactionState().getTripId();
    }
    
    public int generatePathHash() {
        return this.tx.getCrossProcessTransactionState().generatePathHash();
    }
    
    public Integer getReferringPathHash() {
        return this.tx.getInboundHeaderState().getReferringPathHash();
    }
    
    public String getAlternatePathHashes() {
        return this.tx.getCrossProcessTransactionState().getAlternatePathHashes();
    }
    
    public String getSyntheticsResourceId() {
        return this.tx.getInboundHeaderState().getSyntheticsResourceId();
    }
    
    public String getSyntheticsJobId() {
        return this.tx.getInboundHeaderState().getSyntheticsJobId();
    }
    
    public String getSyntheticsMonitorId() {
        return this.tx.getInboundHeaderState().getSyntheticsMonitorId();
    }
    
    public ApdexPerfZone getApdexPerfZone() {
        if (!this.isWebTransaction() && !this.tx.getAgentConfig().isApdexTSet(this.getPriorityTransactionName().getName())) {
            return null;
        }
        final long responseTimeInMillis = this.tx.getTransactionTimer().getResponseTimeInMilliseconds() + this.tx.getExternalTime();
        final long apdexTInMillis = this.tx.getAgentConfig().getApdexTInMillis(this.getPriorityTransactionName().getName());
        return ApdexPerfZone.getZone(responseTimeInMillis, apdexTInMillis);
    }
    
    public boolean isWebTransaction() {
        return this.getDispatcher().isWebTransaction();
    }
    
    public boolean isSyntheticTransaction() {
        return this.tx.isSynthetic();
    }
    
    public SqlTracerListener getSqlTracerListener() {
        return this.tx.getSqlTracerListener();
    }
    
    public String toString() {
        final String name = (this.getRequestUri() == null) ? this.getRootTracer().getMetricName() : this.getRequestUri();
        final StringBuilder builder = new StringBuilder((name == null) ? "" : name).append(' ').append(this.getDurationInMillis()).append("ms");
        if (this.getThrowable() != null) {
            builder.append(' ').append(this.getThrowable().toString());
        }
        return builder.toString();
    }
}
