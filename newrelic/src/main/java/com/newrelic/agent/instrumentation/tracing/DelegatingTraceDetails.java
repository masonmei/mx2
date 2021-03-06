// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.instrumentation.InstrumentationType;
import java.util.List;

public class DelegatingTraceDetails implements TraceDetails
{
    private final TraceDetails delegate;
    
    public DelegatingTraceDetails(final TraceDetails delegate) {
        this.delegate = delegate;
    }
    
    public String metricName() {
        return this.delegate.metricName();
    }
    
    public boolean dispatcher() {
        return this.delegate.dispatcher();
    }
    
    public String tracerFactoryName() {
        return this.delegate.tracerFactoryName();
    }
    
    public boolean excludeFromTransactionTrace() {
        return this.delegate.excludeFromTransactionTrace();
    }
    
    public String metricPrefix() {
        return this.delegate.metricPrefix();
    }
    
    public String getFullMetricName(final String className, final String methodName) {
        return this.delegate.getFullMetricName(className, methodName);
    }
    
    public boolean ignoreTransaction() {
        return this.delegate.ignoreTransaction();
    }
    
    public boolean isCustom() {
        return this.delegate.isCustom();
    }
    
    public TransactionName transactionName() {
        return this.delegate.transactionName();
    }
    
    public List<InstrumentationType> instrumentationTypes() {
        return this.delegate.instrumentationTypes();
    }
    
    public List<String> instrumentationSourceNames() {
        return this.delegate.instrumentationSourceNames();
    }
    
    public boolean isWebTransaction() {
        return this.delegate.isWebTransaction();
    }
    
    public boolean isLeaf() {
        return this.delegate.isLeaf();
    }
    
    public String[] rollupMetricName() {
        return this.delegate.rollupMetricName();
    }
    
    public List<ParameterAttributeName> getParameterAttributeNames() {
        return this.delegate.getParameterAttributeNames();
    }
}
