// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

@LoadOnBootstrap
public interface TransactionHolder
{
    Object _nr_getTransaction();
    
    void _nr_setTransaction(Object p0);
    
    Object _nr_getName();
    
    void _nr_setName(Object p0);
}
