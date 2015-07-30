// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.database;

import java.sql.ResultSetMetaData;

public interface DatabaseStatementParser
{
    public static final String SELECT_OPERATION = "select";
    public static final String INSERT_OPERATION = "insert";
    public static final ParsedDatabaseStatement UNPARSEABLE_STATEMENT = new ParsedDatabaseStatement(null, "other", true);
    
    ParsedDatabaseStatement getParsedDatabaseStatement(String p0, ResultSetMetaData p1);
}
