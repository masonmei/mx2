// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.dispatchers;

import com.newrelic.api.agent.Response;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.transaction.TransactionNamer;
import com.newrelic.agent.transaction.OtherTransactionNamer;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class AsyncDispatcher extends DefaultDispatcher
{
    private final MetricNameFormat uri;
    
    public AsyncDispatcher(final Transaction transaction, final MetricNameFormat uri) {
        super(transaction);
        this.uri = uri;
    }
    
    public Request getRequest() {
        return null;
    }
    
    public String getUri() {
        return this.uri.getMetricName();
    }
    
    public void setTransactionName() {
        final TransactionNamer tn = OtherTransactionNamer.create(this.getTransaction(), this.getUri());
        tn.setTransactionName();
    }
    
    public TransactionTracerConfig getTransactionTracerConfig() {
        return this.getTransaction().getRootTransaction().getAgentConfig().getRequestTransactionTracerConfig();
    }
    
    public boolean isWebTransaction() {
        return true;
    }
    
    public boolean isAsyncTransaction() {
        return true;
    }
    
    public void transactionFinished(final String transactionName, final TransactionStats stats) {
    }
    
    public String getCookieValue(final String name) {
        return null;
    }
    
    public String getHeader(final String name) {
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
