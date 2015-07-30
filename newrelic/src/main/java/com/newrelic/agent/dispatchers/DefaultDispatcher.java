// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.dispatchers;

import com.newrelic.agent.Transaction;

public abstract class DefaultDispatcher implements Dispatcher
{
    private final Transaction transaction;
    private volatile boolean ignoreApdex;
    
    public DefaultDispatcher(final Transaction transaction) {
        this.ignoreApdex = false;
        this.transaction = transaction;
    }
    
    public boolean isAsyncTransaction() {
        return false;
    }
    
    public Transaction getTransaction() {
        return this.transaction;
    }
    
    public boolean isIgnoreApdex() {
        return this.ignoreApdex;
    }
    
    public final void setIgnoreApdex(final boolean ignore) {
        this.ignoreApdex = ignore;
    }
    
    protected String getTransTotalName(final String transactionName, final String rootMetricName) {
        if (transactionName != null && transactionName.indexOf(rootMetricName) == 0) {
            final StringBuilder totalTimeName = new StringBuilder(rootMetricName.length() + rootMetricName.length());
            totalTimeName.append(rootMetricName);
            totalTimeName.append("TotalTime");
            totalTimeName.append(transactionName.substring(rootMetricName.length()));
            return totalTimeName.toString();
        }
        return null;
    }
    
    protected String getApdexMetricName(final String blameMetricName, final String rootMetricName, final String apdexMetricName) {
        if (blameMetricName != null && blameMetricName.indexOf(rootMetricName) == 0) {
            final StringBuilder apdexName = new StringBuilder(apdexMetricName.length() + rootMetricName.length());
            apdexName.append(apdexMetricName);
            apdexName.append(blameMetricName.substring(rootMetricName.length()));
            return apdexName.toString();
        }
        return null;
    }
}
