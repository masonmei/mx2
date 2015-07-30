// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;
import com.newrelic.agent.Agent;
import com.newrelic.agent.TransactionData;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class SqlTracerAggregatorImpl implements SqlTracerAggregator
{
    public static final String BACKTRACE_KEY = "backtrace";
    public static final String EXPLAIN_PLAN_KEY = "explain_plan";
    public static final int SQL_LIMIT_PER_REPORTING_PERIOD = 10;
    static final int MAX_SQL_STATEMENTS = 200;
    private final BoundedConcurrentCache<String, SqlStatementInfo> sqlStatements;
    private final Lock readLock;
    private final Lock writeLock;
    
    public SqlTracerAggregatorImpl() {
        this.sqlStatements = new BoundedConcurrentCache<String, SqlStatementInfo>(200);
        final ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    public List<SqlTrace> getAndClearSqlTracers() {
        List<SqlStatementInfo> infos = null;
        infos = this.getAndClearSqlTracersUnderLock();
        if (infos == null || infos.isEmpty()) {
            return Collections.emptyList();
        }
        return this.createSqlTraces(infos);
    }
    
    public int getSqlInfoCount() {
        return this.sqlStatements.size();
    }
    
    private List<SqlTrace> createSqlTraces(final List<SqlStatementInfo> infos) {
        final List<SqlStatementInfo> topInfos = this.getTopTracers(infos);
        final List<SqlTrace> result = new ArrayList<SqlTrace>(topInfos.size());
        for (final SqlStatementInfo topInfo : topInfos) {
            result.add(topInfo.asSqlTrace());
        }
        return result;
    }
    
    private List<SqlStatementInfo> getTopTracers(final List<SqlStatementInfo> infos) {
        if (infos.size() <= 10) {
            return infos;
        }
        Collections.sort(infos);
        return infos.subList(infos.size() - 10, infos.size());
    }
    
    private List<SqlStatementInfo> getAndClearSqlTracersUnderLock() {
        this.writeLock.lock();
        try {
            final List<SqlStatementInfo> result = this.sqlStatements.asList();
            this.sqlStatements.clear();
            final List<SqlStatementInfo> list = result;
            this.writeLock.unlock();
            return list;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void addSqlTracers(final TransactionData td) {
        final SqlTracerListener listener = td.getSqlTracerListener();
        if (listener == null) {
            Agent.LOG.finest("SqlTracerAggrator: addSqlTracers: no listener");
            return;
        }
        final List<SqlStatementInfo> sqlInfos = listener.getSqlStatementInfo();
        if (sqlInfos.isEmpty()) {
            Agent.LOG.finest("SqlTracerAggrator: addSqlTracers: no sql statement infos");
            return;
        }
        this.addSqlTracersUnderLock(td, sqlInfos);
    }
    
    private void addSqlTracersUnderLock(final TransactionData td, final List<SqlStatementInfo> sqlInfos) {
        this.readLock.lock();
        try {
            for (final SqlStatementInfo sqlInfo : sqlInfos) {
                this.addSqlTracer(td, sqlInfo);
            }
            this.readLock.unlock();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private void addSqlTracer(final TransactionData td, final SqlStatementInfo sqlInfo) {
        final SqlStatementTracer sqlTracer = sqlInfo.getSqlStatementTracer();
        final Object sqlObj = sqlTracer.getSql();
        final String sql = (sqlObj == null) ? null : sqlObj.toString();
        if (sql == null || sql.length() == 0) {
            return;
        }
        final String obfuscatedSql = ObfuscatorUtil.obfuscateSql(sql);
        if (obfuscatedSql == null) {
            return;
        }
        final SqlStatementInfo existingInfo = this.sqlStatements.get(obfuscatedSql);
        if (existingInfo != null) {
            existingInfo.aggregate(sqlInfo);
            this.sqlStatements.putReplace(obfuscatedSql, existingInfo);
        }
        else {
            if (sqlInfo.getTransactionData() == null) {
                sqlInfo.setTransactionData(td);
            }
            this.sqlStatements.putReplace(obfuscatedSql, sqlInfo);
        }
    }
}
