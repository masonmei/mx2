// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;

@InterfaceMixin(originalClassName = { "scala/concurrent/impl/CallbackRunnable", "scala/concurrent/impl/AbstractPromise" })
public interface ScalaTransactionHolder extends TransactionHolder
{
    public static final String CALLBACK_RUNNABLE_CLASS = "scala/concurrent/impl/CallbackRunnable";
    public static final String PROMISE_CLASS = "scala/concurrent/impl/Promise$DefaultPromise";
    public static final String PROMISE_ABSTRACT_CLASS = "scala/concurrent/impl/AbstractPromise";
    public static final String PROMISE_INTERFACE = "scala/concurrent/Promise";
    
    @FieldAccessor(fieldName = "transaction", volatileAccess = true)
    void _nr_setTransaction(Object p0);
    
    @FieldAccessor(fieldName = "transaction", volatileAccess = true)
    Object _nr_getTransaction();
    
    @FieldAccessor(fieldName = "name", volatileAccess = true)
    void _nr_setName(Object p0);
    
    @FieldAccessor(fieldName = "name", volatileAccess = true)
    Object _nr_getName();
}
