// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import com.newrelic.agent.util.StackTraces;
import java.util.logging.Level;
import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import java.util.HashMap;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.ISqlStatementTracer;
import java.util.ArrayList;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.database.SqlObfuscator;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class TransactionSegment implements JSONStreamAware
{
    private static final String PARTIAL_TRACE = "partialtrace";
    private static final Pattern INSERT_INTO_VALUES_STATEMENT;
    private static final String URI_PARAM_NAME = "uri";
    private String metricName;
    private final List<TransactionSegment> children;
    private final long entryTimestamp;
    private long exitTimestamp;
    private final Map<String, Object> tracerAttributes;
    private int callCount;
    private final String uri;
    private final SqlObfuscator sqlObfuscator;
    private final TransactionTracerConfig ttConfig;
    private final List<StackTraceElement> parentStackTrace;
    private final ClassMethodSignature classMethodSignature;
    
    public TransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final long startTime, final Tracer tracer) {
        this(ttConfig, sqlObfuscator, startTime, tracer, null);
    }
    
    TransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final long startTime, final Tracer tracer, final TransactionSegment childSegment) {
        this.callCount = 1;
        this.ttConfig = ttConfig;
        this.sqlObfuscator = sqlObfuscator;
        this.metricName = getMetricName(tracer);
        this.uri = getUri(tracer);
        if (childSegment == null) {
            this.children = Lists.newArrayList();
        }
        else {
            (this.children = new ArrayList<TransactionSegment>(1)).add(childSegment);
        }
        this.entryTimestamp = tracer.getStartTimeInMilliseconds() - startTime;
        this.exitTimestamp = tracer.getEndTimeInMilliseconds() - startTime;
        this.tracerAttributes = this.getTracerAttributes(tracer);
        this.classMethodSignature = tracer.getClassMethodSignature();
        this.parentStackTrace = this.getParentStackTrace(tracer);
    }
    
    private List<StackTraceElement> getParentStackTrace(final Tracer tracer) {
        if (tracer.getParentTracer() != null) {
            return (List<StackTraceElement>)tracer.getParentTracer().getAttribute("backtrace");
        }
        return null;
    }
    
    private Map<String, Object> getTracerAttributes(final Tracer tracer) {
        if (tracer instanceof ISqlStatementTracer) {
            final Object sql = ((ISqlStatementTracer)tracer).getSql();
            if (sql != null) {
                tracer.setAttribute("sql", sql);
            }
        }
        return tracer.getAttributes();
    }
    
    private static String getMetricName(final Tracer tracer) {
        String metricName = tracer.getTransactionSegmentName();
        if (metricName == null || metricName.trim().length() == 0) {
            if (Agent.isDebugEnabled()) {
                throw new RuntimeException(MessageFormat.format("Encountered a transaction segment with an invalid metric name. {0}", tracer.getClass().getName()));
            }
            metricName = tracer.getClass().getName() + "*";
        }
        return metricName;
    }
    
    public Map<String, Object> getTraceParameters() {
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.tracerAttributes);
    }
    
    private static String getUri(final Tracer tracer) {
        return tracer.getTransactionSegmentUri();
    }
    
    void setMetricName(final String name) {
        this.metricName = name;
    }
    
    public Collection<TransactionSegment> getChildren() {
        return Collections.unmodifiableCollection((Collection<? extends TransactionSegment>)this.children);
    }
    
    public String getMetricName() {
        return this.metricName;
    }
    
    public void addChild(final TransactionSegment sample) {
        try {
            this.children.add(sample);
        }
        catch (UnsupportedOperationException e) {
            final String msg = MessageFormat.format("Unable to add transaction segment {0} to parent segment {1}", sample, this);
            Agent.LOG.info(msg);
        }
    }
    
    public String toString() {
        return this.metricName;
    }
    
    public void writeJSONString(final Writer writer) throws IOException {
        final Map<String, Object> params = new HashMap<String, Object>(this.tracerAttributes);
        this.processStackTraces(params);
        this.processSqlParams(params);
        if (this.callCount > 1) {
            params.put("call_count", this.callCount);
        }
        if (this.uri != null && this.uri.length() > 0) {
            params.put("uri", this.uri);
        }
        JSONArray.writeJSONString(Arrays.asList(this.entryTimestamp, this.exitTimestamp, this.metricName, params, this.children, this.classMethodSignature.getClassName(), this.classMethodSignature.getMethodName()), writer);
    }
    
    private void processSqlParams(final Map<String, Object> params) {
        final Object sqlObj = params.remove("sql");
        if (sqlObj == null) {
            return;
        }
        String sql = this.sqlObfuscator.obfuscateSql(sqlObj.toString());
        if (sql == null) {
            return;
        }
        if (TransactionSegment.INSERT_INTO_VALUES_STATEMENT.matcher(sql).matches()) {
            final int maxLength = this.ttConfig.getInsertSqlMaxLength();
            sql = truncateSql(sql, maxLength);
        }
        if (this.ttConfig.isLogSql()) {
            Agent.LOG.log(Level.INFO, MessageFormat.format("{0} SQL: {1}", this.ttConfig.getRecordSql(), sql));
            return;
        }
        params.put(this.sqlObfuscator.isObfuscating() ? "sql_obfuscated" : "sql", sql);
    }
    
    private void processStackTraces(final Map<String, Object> params) {
        final List<StackTraceElement> backtrace = (List<StackTraceElement>) params.remove("backtrace");
        if (backtrace != null) {
            final List<StackTraceElement> preStackTraces = StackTraces.scrubAndTruncate(backtrace);
            final List<String> postParentRemovalTrace = StackTraces.toStringListRemoveParent(preStackTraces, this.parentStackTrace);
            if (preStackTraces.size() == postParentRemovalTrace.size()) {
                params.put("backtrace", postParentRemovalTrace);
            }
            else {
                params.put("partialtrace", postParentRemovalTrace);
            }
        }
    }
    
    public void merge(final Tracer tracer) {
        ++this.callCount;
        this.exitTimestamp += tracer.getDurationInMilliseconds();
    }
    
    public static String truncateSql(final String sql, final int maxLength) {
        final int len = sql.length();
        if (len > maxLength) {
            return MessageFormat.format("{0}..({1} more chars)", sql.substring(0, maxLength), len - maxLength);
        }
        return sql;
    }
    
    static {
        INSERT_INTO_VALUES_STATEMENT = Pattern.compile("\\s*insert\\s+into\\s+([^\\s(,]*)\\s+values.*", 2);
    }
}
