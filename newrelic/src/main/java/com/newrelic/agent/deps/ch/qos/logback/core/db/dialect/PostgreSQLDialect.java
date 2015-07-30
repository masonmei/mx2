// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db.dialect;

public class PostgreSQLDialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "SELECT currval('logging_event_id_seq')";
    
    public String getSelectInsertId() {
        return "SELECT currval('logging_event_id_seq')";
    }
}
