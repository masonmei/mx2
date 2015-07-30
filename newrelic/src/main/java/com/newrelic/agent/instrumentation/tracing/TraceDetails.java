// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.instrumentation.InstrumentationType;
import java.util.List;

public interface TraceDetails
{
    String metricName();
    
    String[] rollupMetricName();
    
    boolean dispatcher();
    
    TransactionName transactionName();
    
    String tracerFactoryName();
    
    boolean excludeFromTransactionTrace();
    
    String metricPrefix();
    
    String getFullMetricName(String p0, String p1);
    
    boolean ignoreTransaction();
    
    List<InstrumentationType> instrumentationTypes();
    
    List<String> instrumentationSourceNames();
    
    boolean isCustom();
    
    boolean isLeaf();
    
    boolean isWebTransaction();
    
    List<ParameterAttributeName> getParameterAttributeNames();
}
