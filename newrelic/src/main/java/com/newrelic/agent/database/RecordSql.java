// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.database;

public enum RecordSql
{
    obfuscated, 
    raw, 
    off;
    
    public static RecordSql get(final String value) {
        return Enum.valueOf(RecordSql.class, value);
    }
}
