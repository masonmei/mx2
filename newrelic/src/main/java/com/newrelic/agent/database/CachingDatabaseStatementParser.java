// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.database;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.sql.ResultSetMetaData;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import com.newrelic.agent.deps.com.google.common.cache.Cache;

public class CachingDatabaseStatementParser implements DatabaseStatementParser
{
    private final DatabaseStatementParser databaseStatementParser;
    private volatile Cache<String, ParsedDatabaseStatement> statements;
    
    public CachingDatabaseStatementParser(final DatabaseStatementParser databaseStatementParser) {
        this.databaseStatementParser = databaseStatementParser;
    }
    
    private Cache<String, ParsedDatabaseStatement> getOrCreateCache() {
        if (null == this.statements) {
            synchronized (this) {
                if (null == this.statements) {
                    this.statements = CacheBuilder.newBuilder().maximumSize(100L).build();
                }
            }
        }
        return this.statements;
    }
    
    public ParsedDatabaseStatement getParsedDatabaseStatement(final String statement, final ResultSetMetaData resultSetMetaData) {
        Throwable toLog = null;
        try {
            return this.getOrCreateCache().get(statement, new Callable<ParsedDatabaseStatement>() {
                public ParsedDatabaseStatement call() throws Exception {
                    return CachingDatabaseStatementParser.this.databaseStatementParser.getParsedDatabaseStatement(statement, resultSetMetaData);
                }
            });
        }
        catch (ExecutionException ee) {
            toLog = ee;
            if (ee.getCause() != null) {
                toLog = ee.getCause();
            }
        }
        catch (Exception ex) {
            toLog = ex;
        }
        Agent.LOG.log(Level.FINEST, "In cache.get() or its loader:", toLog);
        return CachingDatabaseStatementParser.UNPARSEABLE_STATEMENT;
    }
}
