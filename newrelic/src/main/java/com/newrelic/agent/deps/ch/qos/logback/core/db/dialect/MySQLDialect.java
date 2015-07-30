// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db.dialect;

public class MySQLDialect implements SQLDialect
{
    public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";
    
    public String getSelectInsertId() {
        return "SELECT LAST_INSERT_ID()";
    }
}
