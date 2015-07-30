// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.db.names;

public class DefaultDBNameResolver implements DBNameResolver
{
    public <N extends Enum<?>> String getTableName(final N tableName) {
        return tableName.toString().toLowerCase();
    }
    
    public <N extends Enum<?>> String getColumnName(final N columnName) {
        return columnName.toString().toLowerCase();
    }
}
