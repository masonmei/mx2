// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.database.DatabaseVendor;
import java.sql.SQLException;
import java.sql.Connection;

public interface ConnectionFactory
{
    Connection getConnection() throws SQLException;
    
    String getUrl();
    
    DatabaseVendor getDatabaseVendor();
}
