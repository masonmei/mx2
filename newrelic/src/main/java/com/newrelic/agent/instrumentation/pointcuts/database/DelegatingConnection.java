// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import java.sql.Connection;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "com/newrelic/agent/deps/org/apache/commons/dbcp/DelegatingConnection" })
public interface DelegatingConnection
{
    Connection getInnermostDelegate();
    
    Connection getDelegate();
    
    @InterfaceMixin(originalClassName = { "com/newrelic/agent/deps/org/apache/commons/dbcp/PoolingDataSource$PoolGuardConnectionWrapper" })
    public interface PoolGuardConnectionWrapper
    {
        @FieldAccessor(fieldName = "delegate", existingField = true)
        Connection _nr_getDelegate();
    }
}
