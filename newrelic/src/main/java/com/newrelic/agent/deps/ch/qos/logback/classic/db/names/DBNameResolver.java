// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.db.names;

public interface DBNameResolver
{
     <N extends Enum<?>> String getTableName(N p0);
    
     <N extends Enum<?>> String getColumnName(N p0);
}
