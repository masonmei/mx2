// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.TracerFactory;

public interface TransactionState
{
    Tracer getTracer(Transaction p0, TracerFactory p1, ClassMethodSignature p2, Object p3, Object... p4);
    
    Tracer getTracer(Transaction p0, String p1, ClassMethodSignature p2, Object p3, Object... p4);
    
    Tracer getTracer(Transaction p0, Object p1, ClassMethodSignature p2, String p3, int p4);
    
    boolean finish(Transaction p0, Tracer p1);
    
    void resume();
    
    void suspend();
    
    void suspendRootTracer();
    
    void complete();
    
    void asyncJobStarted(TransactionHolder p0);
    
    void asyncJobFinished(TransactionHolder p0);
    
    void asyncTransactionStarted(Transaction p0, TransactionHolder p1);
    
    void asyncTransactionFinished(TransactionActivity p0);
    
    void mergeAsyncTracers();
    
    Tracer getRootTracer();
    
    void asyncJobInvalidate(TransactionHolder p0);
    
    void setInvalidateAsyncJobs(boolean p0);
}
