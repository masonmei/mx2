// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import java.util.Collections;
import java.util.List;
import com.newrelic.agent.TransactionData;
import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;

public class DefaultSqlTracerListener implements SqlTracerListener
{
    private final Object lock;
    private final double thresholdInMillis;
    private volatile BoundedConcurrentCache<String, SqlStatementInfo> sqlInfoCache;
    private static final int MAX_SQL_TRACERS = 175;
    
    public DefaultSqlTracerListener(final double thresholdInMillis) {
        this.lock = new Object();
        this.sqlInfoCache = null;
        this.thresholdInMillis = thresholdInMillis;
    }
    
    public void noticeSqlTracer(final SqlStatementTracer sqlTracer) {
        if (sqlTracer.getDurationInMilliseconds() > this.thresholdInMillis) {
            synchronized (this.lock) {
                if (this.sqlInfoCache == null) {
                    this.sqlInfoCache = new BoundedConcurrentCache<String, SqlStatementInfo>(175);
                }
                final Object sqlObject = sqlTracer.getSql();
                if (sqlObject == null) {
                    return;
                }
                final String sql = sqlObject.toString();
                final String obfuscatedSql = ObfuscatorUtil.obfuscateSql(sql);
                if (obfuscatedSql == null) {
                    return;
                }
                final SqlStatementInfo existingInfo = this.sqlInfoCache.get(obfuscatedSql);
                if (existingInfo != null) {
                    existingInfo.aggregate(sqlTracer);
                    this.sqlInfoCache.putReplace(obfuscatedSql, existingInfo);
                }
                else {
                    final SqlStatementInfo sqlInfo = new SqlStatementInfo(null, sqlTracer, obfuscatedSql.hashCode());
                    sqlInfo.aggregate(sqlTracer);
                    this.sqlInfoCache.putIfAbsent(obfuscatedSql, sqlInfo);
                }
            }
        }
    }
    
    public List<SqlStatementInfo> getSqlStatementInfo() {
        if (this.sqlInfoCache == null) {
            return Collections.emptyList();
        }
        return this.sqlInfoCache.asList();
    }
}
