// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.database.ParsedDatabaseStatement;
import java.sql.Statement;

public interface StatementData
{
    Statement getStatement();
    
    String getSql();
    
    ParsedDatabaseStatement getParsedStatement(Object p0, long p1);
}
