// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db.dialect;

public class H2Dialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "CALL IDENTITY()";
    
    public String getSelectInsertId() {
        return "CALL IDENTITY()";
    }
}
