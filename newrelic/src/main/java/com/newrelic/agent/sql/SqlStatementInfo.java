// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import com.newrelic.agent.util.StackTraces;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.database.DatabaseService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;
import com.newrelic.agent.TransactionData;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class SqlStatementInfo implements Comparable<SqlStatementInfo>
{
    private final AtomicReference<SqlTracerInfo> slowestSql;
    private final int id;
    private final AtomicInteger callCount;
    private final AtomicLong total;
    final AtomicLong max;
    private final AtomicLong min;
    private String cachedSql;
    
    SqlStatementInfo(final TransactionData td, final SqlStatementTracer sqlTracer, final int id) {
        this.slowestSql = new AtomicReference<SqlTracerInfo>();
        this.callCount = new AtomicInteger();
        this.total = new AtomicLong();
        this.max = new AtomicLong();
        this.min = new AtomicLong(Long.MAX_VALUE);
        this.cachedSql = null;
        this.slowestSql.set(new SqlTracerInfo(td, sqlTracer));
        this.id = id;
    }
    
    public TransactionData getTransactionData() {
        return this.slowestSql.get().getTransactionData();
    }
    
    public SqlStatementTracer getSqlStatementTracer() {
        return this.slowestSql.get().getSqlTracer();
    }
    
    public int compareTo(final SqlStatementInfo other) {
        final Long thisMax = this.max.get();
        final Long otherMax = other.max.get();
        final int compare = thisMax.compareTo(otherMax);
        if (compare == 0) {
            return this.getSql().compareTo(other.getSql());
        }
        return compare;
    }
    
    public void aggregate(final SqlStatementTracer sqlTracer) {
        this.aggregate(null, sqlTracer);
    }
    
    public void aggregate(final TransactionData td, final SqlStatementTracer sqlTracer) {
        this.callCount.incrementAndGet();
        final long duration = sqlTracer.getDuration();
        this.total.addAndGet(duration);
        this.replaceMin(duration);
        this.replaceMax(duration);
        this.replaceSqlTracer(td, sqlTracer);
    }
    
    public void aggregate(final SqlStatementInfo other) {
        final long duration = other.getSqlStatementTracer().getDuration();
        this.total.addAndGet(other.getTotalInNano());
        this.callCount.addAndGet(other.getCallCount());
        this.replaceMin(duration);
        this.replaceMax(duration);
        this.replaceSqlTracer(other.getTransactionData(), other.getSqlStatementTracer());
    }
    
    public SqlTrace asSqlTrace() {
        final SqlStatementTracer sqlTracer = this.getSqlStatementTracer();
        final DatabaseService dbService = ServiceFactory.getDatabaseService();
        dbService.runExplainPlan(sqlTracer);
        return new SqlTraceImpl(this);
    }
    
    public String getBlameMetricName() {
        return this.getTransactionData().getBlameMetricName();
    }
    
    public String getMetricName() {
        return this.getSqlStatementTracer().getMetricName();
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getSql() {
        if (this.cachedSql != null) {
            return this.cachedSql;
        }
        final SqlStatementTracer sqlTracer = this.getSqlStatementTracer();
        final String sql = sqlTracer.getSql().toString();
        String obfuscatedSql = null;
        if (this.getTransactionData() != null) {
            final String appName = this.getTransactionData().getApplicationName();
            final SqlObfuscator sqlObfuscator = ServiceFactory.getDatabaseService().getSqlObfuscator(appName);
            obfuscatedSql = sqlObfuscator.obfuscateSql(sql);
        }
        final int maxSqlLength = sqlTracer.getTransaction().getTransactionTracerConfig().getInsertSqlMaxLength();
        return this.cachedSql = TransactionSegment.truncateSql((obfuscatedSql == null) ? sql : obfuscatedSql, maxSqlLength);
    }
    
    public String getRequestUri() {
        return this.getTransactionData().getRequestUri();
    }
    
    public int getCallCount() {
        return this.callCount.get();
    }
    
    public long getTotalInNano() {
        return this.total.get();
    }
    
    public long getTotalInMillis() {
        return TimeUnit.MILLISECONDS.convert(this.total.get(), TimeUnit.NANOSECONDS);
    }
    
    public long getMinInMillis() {
        return TimeUnit.MILLISECONDS.convert(this.min.get(), TimeUnit.NANOSECONDS);
    }
    
    public long getMaxInMillis() {
        return TimeUnit.MILLISECONDS.convert(this.max.get(), TimeUnit.NANOSECONDS);
    }
    
    public Map<String, Object> getParameters() {
        final SqlStatementTracer sqlTracer = this.getSqlStatementTracer();
        return this.createParameters(sqlTracer);
    }
    
    private Map<String, Object> createParameters(final SqlStatementTracer sqlTracer) {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        final Object explainPlan = sqlTracer.getAttribute("explanation");
        parameters.put("explain_plan", explainPlan);
        List<StackTraceElement> backtrace = (List<StackTraceElement>)sqlTracer.getAttribute("backtrace");
        if (backtrace != null) {
            backtrace = StackTraces.scrubAndTruncate(backtrace);
            final List<String> backtraceStrings = StackTraces.toStringList(backtrace);
            parameters.put("backtrace", backtraceStrings);
        }
        return parameters;
    }
    
    private void replaceMin(final long duration) {
        while (true) {
            final long currentDuration = this.min.get();
            if (duration >= currentDuration) {
                return;
            }
            if (this.min.compareAndSet(currentDuration, duration)) {
                return;
            }
        }
    }
    
    private void replaceMax(final long duration) {
        while (true) {
            final long currentDuration = this.max.get();
            if (duration <= currentDuration) {
                return;
            }
            if (this.max.compareAndSet(currentDuration, duration)) {
                return;
            }
        }
    }
    
    private void replaceSqlTracer(final TransactionData td, final SqlStatementTracer sqlTracer) {
        while (true) {
            final SqlTracerInfo current = this.slowestSql.get();
            if (sqlTracer.getDuration() <= current.getSqlTracer().getDuration()) {
                return;
            }
            final SqlTracerInfo update = new SqlTracerInfo(td, sqlTracer);
            if (this.slowestSql.compareAndSet(current, update)) {
                return;
            }
        }
    }
    
    public void setTransactionData(final TransactionData td) {
        this.slowestSql.get().setTransactionData(td);
    }
}
