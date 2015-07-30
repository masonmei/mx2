// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.dispatchers;

import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.stats.ApdexStats;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.transaction.TransactionNamer;
import com.newrelic.agent.transaction.OtherTransactionNamer;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class OtherDispatcher extends DefaultDispatcher
{
    private final MetricNameFormat uri;
    
    public OtherDispatcher(final Transaction transaction, final MetricNameFormat uri) {
        super(transaction);
        this.uri = uri;
    }
    
    public void setTransactionName() {
        final TransactionNamer tn = OtherTransactionNamer.create(this.getTransaction(), this.getUri());
        tn.setTransactionName();
    }
    
    public String getUri() {
        return this.uri.getMetricName();
    }
    
    public TransactionTracerConfig getTransactionTracerConfig() {
        return this.getTransaction().getAgentConfig().getBackgroundTransactionTracerConfig();
    }
    
    public boolean isAsyncTransaction() {
        return false;
    }
    
    public void transactionFinished(final String transactionName, final TransactionStats stats) {
        stats.getUnscopedStats().getResponseTimeStats(transactionName).recordResponseTime(this.getTransaction().getTransactionTimer().getResponseTime(), 0L, TimeUnit.NANOSECONDS);
        final String totalTimeMetric = this.getTransTotalName(transactionName, "OtherTransaction");
        if (totalTimeMetric != null && totalTimeMetric.length() > 0) {
            stats.getUnscopedStats().getResponseTimeStats(totalTimeMetric).recordResponseTime(this.getTransaction().getTransactionTimer().getTotalTime(), 0L, TimeUnit.NANOSECONDS);
        }
        stats.getUnscopedStats().getResponseTimeStats("OtherTransaction/all").recordResponseTime(this.getTransaction().getTransactionTimer().getResponseTime(), this.getTransaction().getTransactionTimer().getResponseTime(), TimeUnit.NANOSECONDS);
        stats.getUnscopedStats().getResponseTimeStats("OtherTransactionTotalTime").recordResponseTime(this.getTransaction().getTransactionTimer().getTotalTime(), this.getTransaction().getTransactionTimer().getTotalTime(), TimeUnit.NANOSECONDS);
        this.recordApdexMetrics(transactionName, stats);
    }
    
    private void recordApdexMetrics(final String transactionName, final TransactionStats stats) {
        if (transactionName == null || transactionName.length() == 0) {
            return;
        }
        if (!this.getTransaction().getAgentConfig().isApdexTSet(transactionName)) {
            return;
        }
        if (this.isIgnoreApdex()) {
            Agent.LOG.log(Level.FINE, "Ignoring transaction for apdex {0}", new Object[] { transactionName });
            return;
        }
        final String apdexMetricName = this.getApdexMetricName(transactionName, "OtherTransaction", "ApdexOther/Transaction");
        if (apdexMetricName == null || apdexMetricName.length() == 0) {
            return;
        }
        final long apdexT = this.getTransaction().getAgentConfig().getApdexTInMillis(transactionName);
        final ApdexStats apdexStats = stats.getUnscopedStats().getApdexStats(apdexMetricName);
        final ApdexStats overallApdexStats = stats.getUnscopedStats().getApdexStats("ApdexOther");
        final long responseTimeInMillis = this.getTransaction().getTransactionTimer().getResponseTimeInMilliseconds();
        apdexStats.recordApdexResponseTime(responseTimeInMillis, apdexT);
        overallApdexStats.recordApdexResponseTime(responseTimeInMillis, apdexT);
    }
    
    public boolean isWebTransaction() {
        return false;
    }
    
    public String getCookieValue(final String name) {
        return null;
    }
    
    public String getHeader(final String name) {
        return null;
    }
    
    public Request getRequest() {
        return null;
    }
    
    public void setRequest(final Request request) {
    }
    
    public Response getResponse() {
        return null;
    }
    
    public void setResponse(final Response response) {
    }
}
