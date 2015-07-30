// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db;

import com.newrelic.agent.deps.ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.sql.SQLException;
import java.sql.Connection;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;

public interface ConnectionSource extends LifeCycle
{
    Connection getConnection() throws SQLException;
    
    SQLDialectCode getSQLDialectCode();
    
    boolean supportsGetGeneratedKeys();
    
    boolean supportsBatchUpdates();
}
