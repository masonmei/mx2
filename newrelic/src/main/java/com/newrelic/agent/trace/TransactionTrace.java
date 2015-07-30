// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import com.newrelic.agent.transport.DataSenderWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.io.Writer;
import com.newrelic.agent.attributes.AttributesUtils;
import com.newrelic.agent.database.DatabaseVendor;
import java.sql.Connection;
import com.newrelic.agent.database.DatabaseService;
import java.util.logging.Level;
import com.newrelic.agent.instrumentation.pointcuts.database.DatabaseUtils;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedList;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.HashMap;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.TransactionData;
import java.util.Collection;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.instrumentation.pointcuts.database.ExplainPlanExecutor;
import com.newrelic.agent.instrumentation.pointcuts.database.ConnectionFactory;
import java.util.Map;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class TransactionTrace implements Comparable<TransactionTrace>, JSONStreamAware
{
    private final TransactionSegment rootSegment;
    private final List<TransactionSegment> sqlSegments;
    private final Map<ConnectionFactory, List<ExplainPlanExecutor>> sqlTracers;
    private final long duration;
    private final long startTime;
    private String requestUri;
    private final String rootMetricName;
    private final Map<String, Object> userAttributes;
    private final Map<String, Object> agentAttributes;
    private final Map<String, Object> intrinsicAttributes;
    private final long rootTracerStartTime;
    private Map<Tracer, Collection<Tracer>> children;
    private final String guid;
    private final Map<String, Map<String, String>> prefixedAttributes;
    private Long xraySessionId;
    private String syntheticsResourceId;
    private final String applicationName;
    
    private TransactionTrace(final TransactionData transactionData, final SqlObfuscator sqlObfuscator) {
        this.applicationName = transactionData.getApplicationName();
        this.children = buildChildren(transactionData.getTracers());
        this.sqlTracers = new HashMap<ConnectionFactory, List<ExplainPlanExecutor>>();
        final Tracer tracer = transactionData.getRootTracer();
        this.userAttributes = Maps.newHashMap();
        this.agentAttributes = Maps.newHashMap();
        if (ServiceFactory.getAttributesService().isAttributesEnabledForTraces(this.applicationName)) {
            if (transactionData.getAgentAttributes() != null) {
                this.agentAttributes.putAll(transactionData.getAgentAttributes());
            }
            if (transactionData.getUserAttributes() != null) {
                this.userAttributes.putAll(transactionData.getUserAttributes());
            }
        }
        this.prefixedAttributes = transactionData.getPrefixedAttributes();
        this.intrinsicAttributes = Maps.newHashMap();
        if (transactionData.getIntrinsicAttributes() != null) {
            this.intrinsicAttributes.putAll(transactionData.getIntrinsicAttributes());
        }
        this.startTime = transactionData.getWallClockStartTimeMs();
        this.rootTracerStartTime = tracer.getStartTimeInMilliseconds();
        this.sqlSegments = new LinkedList<TransactionSegment>();
        this.requestUri = transactionData.getRequestUri();
        if (this.requestUri == null || this.requestUri.length() == 0) {
            this.requestUri = "/ROOT";
        }
        this.rootMetricName = transactionData.getBlameOrRootMetricName();
        this.guid = transactionData.getGuid();
        (this.rootSegment = new TransactionSegment(transactionData.getTransactionTracerConfig(), sqlObfuscator, this.rootTracerStartTime, tracer, this.createTransactionSegment(transactionData.getTransactionTracerConfig(), sqlObfuscator, tracer, null))).setMetricName("ROOT");
        this.duration = tracer.getDurationInMilliseconds();
        final Long gcTime = (Long)this.intrinsicAttributes.remove("gc_time");
        if (gcTime != null) {
            final float gcTimeInSecs = gcTime / 1.0E9f;
            this.intrinsicAttributes.put("gc_time", gcTimeInSecs);
        }
        final Long cpuTime = (Long)this.intrinsicAttributes.remove("cpu_time");
        if (cpuTime != null) {
            final float cpuTimeInSecs = cpuTime / 1.0E9f;
            this.intrinsicAttributes.put("cpu_time", cpuTimeInSecs);
        }
        this.children.clear();
        this.children = null;
        this.xraySessionId = null;
        this.syntheticsResourceId = null;
    }
    
    private static Map<Tracer, Collection<Tracer>> buildChildren(final Collection<Tracer> tracers) {
        if (tracers == null || tracers.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<Tracer, Collection<Tracer>> children = new HashMap<Tracer, Collection<Tracer>>();
        for (final Tracer tracer : tracers) {
            final Tracer parentTracer = tracer.getParentTracer();
            Collection<Tracer> kids = children.get(parentTracer);
            if (kids == null) {
                kids = new LinkedList<Tracer>();
                children.put(parentTracer, kids);
            }
            kids.add(tracer);
        }
        return children;
    }
    
    private static SqlObfuscator getSqlObfuscator(final String appName) {
        final SqlObfuscator sqlObfuscator = ServiceFactory.getDatabaseService().getSqlObfuscator(appName);
        return SqlObfuscator.getCachingSqlObfuscator(sqlObfuscator);
    }
    
    public static TransactionTrace getTransactionTrace(final TransactionData td) {
        return getTransactionTrace(td, getSqlObfuscator(td.getApplicationName()));
    }
    
    static TransactionTrace getTransactionTrace(final TransactionData transactionData, final SqlObfuscator sqlObfuscator) {
        return new TransactionTrace(transactionData, sqlObfuscator);
    }
    
    public TransactionSegment getRootSegment() {
        return this.rootSegment;
    }
    
    private TransactionSegment createTransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final Tracer tracer, final TransactionSegment lastSibling) {
        final TransactionSegment segment = tracer.getTransactionSegment(ttConfig, sqlObfuscator, this.rootTracerStartTime, lastSibling);
        this.processSqlTracer(tracer);
        final Collection<Tracer> children = this.children.get(tracer);
        if (children != null) {
            TransactionSegment lastKid = null;
            for (final Tracer child : children) {
                if (child.getTransactionSegmentName() != null) {
                    final TransactionSegment childSegment = this.createTransactionSegment(ttConfig, sqlObfuscator, child, lastKid);
                    if (childSegment == lastKid) {
                        continue;
                    }
                    this.addChildSegment(segment, childSegment);
                    lastKid = childSegment;
                }
            }
        }
        return segment;
    }
    
    public Map<ConnectionFactory, List<ExplainPlanExecutor>> getExplainPlanExecutors() {
        return Collections.unmodifiableMap((Map<? extends ConnectionFactory, ? extends List<ExplainPlanExecutor>>)this.sqlTracers);
    }
    
    private void processSqlTracer(final Tracer tracer) {
        if (tracer instanceof SqlStatementTracer) {
            final SqlStatementTracer sqlTracer = (SqlStatementTracer)tracer;
            final ExplainPlanExecutor explainExecutor = sqlTracer.getExplainPlanExecutor();
            final ConnectionFactory connectionFactory = sqlTracer.getConnectionFactory();
            if (!sqlTracer.hasExplainPlan() && explainExecutor != null && connectionFactory != null) {
                List<ExplainPlanExecutor> tracers = this.sqlTracers.get(connectionFactory);
                if (tracers == null) {
                    tracers = new LinkedList<ExplainPlanExecutor>();
                    this.sqlTracers.put(connectionFactory, tracers);
                }
                tracers.add(explainExecutor);
            }
        }
    }
    
    private void addChildSegment(final TransactionSegment parent, final TransactionSegment child) {
        if (child.getMetricName() == null) {
            for (final TransactionSegment kid : child.getChildren()) {
                this.addChildSegment(parent, kid);
            }
        }
        else {
            parent.addChild(child);
        }
    }
    
    private void runExplainPlans() {
        if (!this.sqlTracers.isEmpty()) {
            final DatabaseService dbService = ServiceFactory.getDatabaseService();
            for (final Map.Entry<ConnectionFactory, List<ExplainPlanExecutor>> entry : this.sqlTracers.entrySet()) {
                Agent.LOG.finer(MessageFormat.format("Running {0} explain plan(s)", entry.getValue().size()));
                Connection connection = null;
                try {
                    connection = entry.getKey().getConnection();
                    final DatabaseVendor vendor = DatabaseUtils.getDatabaseVendor(connection);
                    for (final ExplainPlanExecutor explainExecutor : entry.getValue()) {
                        if (explainExecutor != null) {
                            explainExecutor.runExplainPlan(dbService, connection, vendor);
                        }
                    }
                }
                catch (Throwable t) {
                    final String msg = MessageFormat.format("An error occurred executing an explain plan: {0}", t.toString());
                    if (Agent.LOG.isLoggable(Level.FINER)) {
                        Agent.LOG.log(Level.FINER, msg, t);
                    }
                    else {
                        Agent.LOG.fine(msg);
                    }
                    if (connection == null) {
                        continue;
                    }
                    try {
                        connection.close();
                    }
                    catch (Exception e) {
                        Agent.LOG.log(Level.FINER, "Unable to close connection", e);
                    }
                }
                finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (Exception e2) {
                            Agent.LOG.log(Level.FINER, "Unable to close connection", e2);
                        }
                    }
                }
            }
            this.sqlTracers.clear();
        }
    }
    
    private Map<String, Object> getAgentAtts() {
        final Map<String, Object> atts = Maps.newHashMap();
        atts.putAll(this.agentAttributes);
        if (this.prefixedAttributes != null && !this.prefixedAttributes.isEmpty()) {
            atts.putAll(AttributesUtils.appendAttributePrefixes(this.prefixedAttributes));
        }
        return atts;
    }
    
    private void filterAndAddIfNotEmpty(final String key, final Map<String, Object> wheretoAdd, final Map<String, Object> toAdd) {
        final Map<String, ?> output = ServiceFactory.getAttributesService().filterTraceAttributes(this.applicationName, toAdd);
        if (output != null && !output.isEmpty()) {
            wheretoAdd.put(key, output);
        }
    }
    
    private Map<String, Object> getAttributes() {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        if (ServiceFactory.getAttributesService().isAttributesEnabledForTraces(this.applicationName)) {
            this.filterAndAddIfNotEmpty("agentAttributes", attributes, this.getAgentAtts());
            if (!ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity()) {
                this.filterAndAddIfNotEmpty("userAttributes", attributes, this.userAttributes);
            }
        }
        if (this.intrinsicAttributes != null && !this.intrinsicAttributes.isEmpty()) {
            attributes.put("intrinsics", this.intrinsicAttributes);
        }
        return attributes;
    }
    
    public void writeJSONString(final Writer writer) throws IOException {
        this.runExplainPlans();
        final boolean forcePersist = false;
        final List<Object> data = Arrays.asList(this.startTime, Collections.EMPTY_MAP, Collections.EMPTY_MAP, this.rootSegment, this.getAttributes());
        if (null == this.xraySessionId && null == this.syntheticsResourceId) {
            JSONArray.writeJSONString(Arrays.asList(this.startTime, this.duration, this.rootMetricName, this.requestUri, DataSenderWriter.getJsonifiedCompressedEncodedString(data, writer), this.guid, null, false), writer);
        }
        else if (null == this.syntheticsResourceId) {
            JSONArray.writeJSONString(Arrays.asList(this.startTime, this.duration, this.rootMetricName, this.requestUri, DataSenderWriter.getJsonifiedCompressedEncodedString(data, writer), this.guid, null, true, this.xraySessionId), writer);
        }
        else {
            JSONArray.writeJSONString(Arrays.asList(this.startTime, this.duration, this.rootMetricName, this.requestUri, DataSenderWriter.getJsonifiedCompressedEncodedString(data, writer), this.guid, null, true, this.xraySessionId, this.syntheticsResourceId), writer);
        }
    }
    
    protected List<TransactionSegment> getSQLSegments() {
        return this.sqlSegments;
    }
    
    public String toString() {
        return MessageFormat.format("{0} {1} ms", this.requestUri, this.duration);
    }
    
    public int compareTo(final TransactionTrace o) {
        return (int)(this.duration - o.duration);
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public String getRequestUri() {
        return this.requestUri;
    }
    
    public void setXraySessionId(final Long xraySessionId) {
        this.xraySessionId = xraySessionId;
    }
    
    public Long getXraySessionId() {
        return this.xraySessionId;
    }
    
    public void setSyntheticsResourceId(final String syntheticsResourceId) {
        this.syntheticsResourceId = syntheticsResourceId;
    }
    
    public String getSyntheticsResourceId() {
        return this.syntheticsResourceId;
    }
    
    public String getRootMetricName() {
        return this.rootMetricName;
    }
    
    public String getApplicationName() {
        return this.applicationName;
    }
}
