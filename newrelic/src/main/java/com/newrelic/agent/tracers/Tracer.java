// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.util.Map;
import com.newrelic.agent.bridge.ExitTracer;
import java.lang.reflect.InvocationHandler;

public interface Tracer extends InvocationHandler, TimedItem, ExitTracer
{
    long getStartTime();
    
    long getStartTimeInMilliseconds();
    
    long getEndTime();
    
    long getEndTimeInMilliseconds();
    
    long getExclusiveDuration();
    
    long getRunningDurationInNanos();
    
    String getMetricName();
    
    String getTransactionSegmentName();
    
    String getTransactionSegmentUri();
    
    Map<String, Object> getAttributes();
    
    void setAttribute(String p0, Object p1);
    
    Object getAttribute(String p0);
    
    void childTracerFinished(Tracer p0);
    
    Tracer getParentTracer();
    
    void setParentTracer(Tracer p0);
    
    boolean isParent();
    
    boolean isMetricProducer();
    
    ClassMethodSignature getClassMethodSignature();
    
    boolean isTransactionSegment();
    
    boolean isChildHasStackTrace();
    
    TransactionSegment getTransactionSegment(TransactionTracerConfig p0, SqlObfuscator p1, long p2, TransactionSegment p3);
    
    boolean isLeaf();
}
