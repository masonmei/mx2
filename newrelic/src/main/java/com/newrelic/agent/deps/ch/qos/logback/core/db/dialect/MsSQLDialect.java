// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db.dialect;

public class MsSQLDialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "SELECT @@identity id";
    
    public String getSelectInsertId() {
        return "SELECT @@identity id";
    }
}
