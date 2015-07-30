// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.sql.ResultSetMetaData;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import com.newrelic.agent.database.ParsedDatabaseStatement;
import com.newrelic.agent.database.DatabaseStatementParser;
import java.sql.Statement;

public class DefaultStatementData implements StatementData
{
    private final Statement statement;
    private final DatabaseStatementParser databaseStatementParser;
    private volatile ParsedDatabaseStatement parsedDatabaseStatement;
    private volatile long timestamp;
    private final String sql;
    static final Pattern SPLIT_STATEMENT_PATTERN;
    
    public DefaultStatementData(final DatabaseStatementParser databaseStatementParser, final Statement statement, final String sql) {
        this(databaseStatementParser, statement, sql, null);
    }
    
    public DefaultStatementData(final DatabaseStatementParser databaseStatementParser, final Statement statement, final String sql, final ParsedDatabaseStatement parsedStatement) {
        this.databaseStatementParser = databaseStatementParser;
        this.sql = sql;
        this.statement = statement;
        this.parsedDatabaseStatement = parsedStatement;
    }
    
    static String getFirstSqlStatement(final String sql) {
        final int index = sql.indexOf(59);
        if (index > 0) {
            return sql.substring(0, index);
        }
        return sql;
    }
    
    public String getSql() {
        return this.sql;
    }
    
    public Statement getStatement() {
        return this.statement;
    }
    
    public StatementData finalizeStatementData() {
        return this;
    }
    
    public ParsedDatabaseStatement getParsedStatement(final Object returnValue, final long configTimestamp) {
        if (this.parsedDatabaseStatement == null) {
            ResultSetMetaData metaData = null;
            try {
                if (returnValue instanceof ResultSet) {
                    metaData = ((ResultSet)returnValue).getMetaData();
                }
            }
            catch (Exception e) {
                if (Agent.isDebugEnabled()) {
                    Agent.LOG.log(Level.FINER, "Unable to get the result set meta data from a statement", e);
                }
            }
            this.timestamp = System.nanoTime();
            this.parsedDatabaseStatement = this.databaseStatementParser.getParsedDatabaseStatement(this.getSql(), metaData);
        }
        else if (configTimestamp > this.timestamp) {
            this.parsedDatabaseStatement = null;
            this.timestamp = 0L;
            return this.getParsedStatement(returnValue, configTimestamp);
        }
        return this.parsedDatabaseStatement;
    }
    
    static {
        SPLIT_STATEMENT_PATTERN = Pattern.compile(";");
    }
}
