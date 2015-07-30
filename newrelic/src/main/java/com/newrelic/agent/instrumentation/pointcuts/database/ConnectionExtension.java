// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;

public interface ConnectionExtension
{
    @FieldAccessor(fieldName = "connectionFactory")
    ConnectionFactory _nr_getConnectionFactory();
    
    @FieldAccessor(fieldName = "connectionFactory")
    void _nr_setConnectionFactory(ConnectionFactory p0);
}
