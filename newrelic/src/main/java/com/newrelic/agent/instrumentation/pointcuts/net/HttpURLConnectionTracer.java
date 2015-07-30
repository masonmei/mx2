// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.net;

import com.newrelic.agent.stats.TransactionStats;
import java.net.HttpURLConnection;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.IOTracer;
import com.newrelic.agent.tracers.AbstractCrossProcessTracer;

public class HttpURLConnectionTracer extends AbstractCrossProcessTracer implements IOTracer
{
    public HttpURLConnectionTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final String methodName) {
        super(transaction, sig, object, host, library, uri, methodName);
    }
    
    protected String getHeaderValue(final Object returnValue, final String name) {
        final HttpURLConnection connection = (HttpURLConnection)this.getInvocationTarget();
        return connection.getHeaderField(name);
    }
    
    protected void doRecordMetrics(final TransactionStats transactionStats) {
        this.doRecordCrossProcessRollup(transactionStats);
    }
}
