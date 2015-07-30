// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.netty;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;

@InterfaceMixin(originalClassName = { "org/jboss/netty/channel/socket/nio/NioAcceptedSocketChannel" })
public interface NettyTransactionHolder extends TransactionHolder
{
    public static final String CLASS = "org/jboss/netty/channel/socket/nio/NioAcceptedSocketChannel";
    
    @FieldAccessor(fieldName = "transaction", volatileAccess = true)
    void _nr_setTransaction(Object p0);
    
    @FieldAccessor(fieldName = "transaction", volatileAccess = true)
    Object _nr_getTransaction();
    
    @FieldAccessor(fieldName = "name", volatileAccess = true)
    void _nr_setName(Object p0);
    
    @FieldAccessor(fieldName = "name", volatileAccess = true)
    Object _nr_getName();
}
