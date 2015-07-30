// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.AbstractExternalComponentTracer;
import com.newrelic.agent.deps.org.json.simple.JSONValue;
import com.newrelic.api.agent.HeaderType;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.io.Writer;
import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import com.newrelic.agent.service.ServiceUtils;
import java.io.Serializable;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.CrossProcessNameFormat;
import com.newrelic.agent.bridge.TracedMethod;
import java.util.logging.Level;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import com.newrelic.agent.util.Obfuscator;
import java.util.Iterator;
import com.newrelic.api.agent.Response;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.api.agent.OutboundHeaders;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.MapMaker;
import java.util.Set;

public class CrossProcessTransactionStateImpl implements CrossProcessTransactionState
{
    private static final boolean OPTIMISTIC_TRACING = false;
    private static final int ALTERNATE_PATH_HASH_MAX_COUNT = 10;
    private static final String UNKNOWN_HOST = "Unkown";
    private final ITransaction tx;
    private final Object lock;
    private volatile String tripId;
    private volatile boolean isCatOriginator;
    private final Set<String> alternatePathHashes;
    private volatile boolean processOutboundResponseDone;
    
    private CrossProcessTransactionStateImpl(final ITransaction tx) {
        this.isCatOriginator = false;
        this.processOutboundResponseDone = false;
        this.tx = tx;
        if (tx instanceof Transaction) {
            this.lock = ((Transaction)tx).getLock();
        }
        else {
            this.lock = new Object();
        }
        final MapMaker factory = new MapMaker().initialCapacity(8).concurrencyLevel(4);
        this.alternatePathHashes = Sets.newSetFromMap(new LazyMapImpl<String, Boolean>(factory));
    }
    
    public void writeResponseHeaders() {
        if (this.tx.isIgnore()) {
            return;
        }
        final Dispatcher dispatcher = this.tx.getDispatcher();
        if (dispatcher == null) {
            return;
        }
        final Response response = dispatcher.getResponse();
        final long contentLength = this.tx.getInboundHeaderState().getRequestContentLength();
        this.processOutboundResponseHeaders((OutboundHeaders)response, contentLength);
    }
    
    public void processOutboundResponseHeaders(final OutboundHeaders outboundHeaders, final long contentLength) {
        if (outboundHeaders != null) {
            final OutboundHeadersMap metadata = new OutboundHeadersMap(outboundHeaders.getHeaderType());
            final boolean populated = this.populateResponseMetadata((OutboundHeaders)metadata, contentLength);
            if (populated && this.obfuscateMetadata(metadata)) {
                for (final Map.Entry<String, String> entry : metadata.entrySet()) {
                    outboundHeaders.setHeader((String)entry.getKey(), (String)entry.getValue());
                }
            }
        }
    }
    
    private boolean obfuscateMetadata(final Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return false;
        }
        final String encodingKey = this.tx.getCrossProcessConfig().getEncodingKey();
        if (encodingKey == null) {
            Agent.LOG.finer("Metadata obfuscation failed. Encoding key is null");
            return false;
        }
        for (final Map.Entry<String, String> entry : metadata.entrySet()) {
            try {
                final String obfuscatedValue = Obfuscator.obfuscateNameUsingKey(entry.getValue(), encodingKey);
                entry.setValue(obfuscatedValue);
            }
            catch (UnsupportedEncodingException e) {
                Agent.LOG.finest(MessageFormat.format("Metadata obfuscation failed. {0}", e));
                return false;
            }
        }
        return true;
    }
    
    private boolean populateResponseMetadata(final OutboundHeaders headers, final long contentLength) {
        if (!this.tx.getCrossProcessConfig().isCrossApplicationTracing()) {
            return false;
        }
        synchronized (this.lock) {
            if (this.tx.isIgnore() || !this.tx.getInboundHeaderState().isTrustedCatRequest() || this.processOutboundResponseDone) {
                return false;
            }
            this.tx.freezeTransactionName();
            final long durationInNanos = this.tx.getRunningDurationInNanos();
            this.recordClientApplicationMetric(durationInNanos);
            this.writeCrossProcessAppDataResponseHeader(headers, durationInNanos, contentLength);
            this.processOutboundResponseDone = true;
        }
        return true;
    }
    
    public void processOutboundRequestHeaders(final OutboundHeaders outboundHeaders) {
        if (outboundHeaders != null) {
            final OutboundHeadersMap metadata = new OutboundHeadersMap(outboundHeaders.getHeaderType());
            this.populateRequestMetadata((OutboundHeaders)metadata);
            if (this.obfuscateMetadata(metadata)) {
                for (final Map.Entry<String, String> entry : metadata.entrySet()) {
                    outboundHeaders.setHeader((String)entry.getKey(), (String)entry.getValue());
                }
            }
        }
    }
    
    public void populateRequestMetadata(final OutboundHeaders headers) {
        if (this.tx.getInboundHeaderState().isTrustedSyntheticsRequest() && this.tx.isInProgress() && !this.tx.isIgnore()) {
            final String synHeader = this.tx.getInboundHeaderState().getUnparsedSyntheticsHeader();
            if (synHeader != null) {
                HeadersUtil.setSyntheticsHeader(headers, synHeader);
            }
        }
        if (!this.tx.getCrossProcessConfig().isCrossApplicationTracing()) {
            return;
        }
        synchronized (this.lock) {
            if (null == this.tx.getDispatcher() || this.tx.isIgnore()) {
                return;
            }
            final String crossProcessId = this.tx.getCrossProcessConfig().getEncodedCrossProcessId();
            if (crossProcessId != null) {
                if (Agent.LOG.isFinerEnabled()) {
                    Agent.LOG.log(Level.FINER, "Sending ID header: {0}", new Object[] { crossProcessId });
                }
                this.isCatOriginator = true;
                HeadersUtil.setIdHeader(headers, this.tx.getCrossProcessConfig().getCrossProcessId());
                final String transactionHeaderValue = this.getTransactionHeaderValue();
                HeadersUtil.setTransactionHeader(headers, transactionHeaderValue);
            }
        }
    }
    
    private void doProcessInboundResponseHeaders(final TracedMethod tracer, final CrossProcessNameFormat crossProcessFormat, final String host, final boolean addRollupMetrics) {
        if (crossProcessFormat != null) {
            if (tracer instanceof DefaultTracer) {
                final DefaultTracer dt = (DefaultTracer)tracer;
                final String transactionId = crossProcessFormat.getTransactionId();
                if (transactionId != null && transactionId.length() > 0) {
                    dt.setAttribute("transaction_guid", transactionId);
                }
                dt.setMetricNameFormat(crossProcessFormat);
                if (Agent.LOG.isFinestEnabled()) {
                    Agent.LOG.log(Level.FINEST, "Received APP_DATA cross process response header for external call: {0}", new Object[] { crossProcessFormat.toString() });
                }
            }
            if (addRollupMetrics && !"Unkown".equals(host)) {
                tracer.addRollupMetricName(new String[] { crossProcessFormat.getHostCrossProcessIdRollupMetricName() });
            }
        }
        if (addRollupMetrics) {
            tracer.addRollupMetricName(new String[] { "External", host, "all" });
            tracer.addRollupMetricName(new String[] { "External/all" });
            if (Transaction.getTransaction().isWebTransaction()) {
                tracer.addRollupMetricName(new String[] { "External/allWeb" });
            }
            else {
                tracer.addRollupMetricName(new String[] { "External/allOther" });
            }
        }
    }
    
    public void processInboundResponseHeaders(final InboundHeaders inboundHeaders, final TracedMethod tracer, final String host, final String uri, final boolean addRollupMetrics) {
        if (!this.tx.getCrossProcessConfig().isCrossApplicationTracing()) {
            return;
        }
        if (inboundHeaders == null || tracer == null) {
            return;
        }
        final String encodedAppData = HeadersUtil.getAppDataHeader(inboundHeaders);
        final String encodingKey = this.tx.getCrossProcessConfig().getEncodingKey();
        final CrossProcessNameFormat crossProcessFormat = CrossProcessNameFormat.create(host, uri, encodedAppData, encodingKey);
        this.doProcessInboundResponseHeaders(tracer, crossProcessFormat, host, addRollupMetrics);
    }
    
    synchronized String getTransactionHeaderValue() {
        synchronized (this.lock) {
            final String json = this.getTransactionHeaderJson(this.tx.getGuid(), this.getForceTransactionTrace(), this.getTripId(), this.generatePathHash());
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Sending TRANSACTION header: {0} obfuscated: {1}", new Object[] { json });
            }
            return json;
        }
    }
    
    private String getTransactionHeaderJson(final String guid, final boolean forceTransactionTrace, final String trip, final int pathHash) {
        final List<?> args = Arrays.asList(guid, forceTransactionTrace, trip, ServiceUtils.intToHexString(pathHash));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(out);
        try {
            JSONArray.writeJSONString(args, writer);
            writer.close();
            return out.toString();
        }
        catch (IOException e) {
            final String msg = MessageFormat.format("Error getting JSON: {0}", e);
            Agent.LOG.error(msg);
            return null;
        }
    }
    
    private void writeCrossProcessAppDataResponseHeader(final OutboundHeaders headers, final long durationInNanos, final long contentLength) {
        final String json = this.getCrossProcessAppDataJson(durationInNanos, contentLength);
        if (Agent.LOG.isLoggable(Level.FINER)) {
            Agent.LOG.log(Level.FINER, "Setting APP_DATA response header to: {0}", new Object[] { json });
        }
        if (json == null) {
            return;
        }
        HeadersUtil.setAppDataHeader(headers, json);
    }
    
    private String getCrossProcessAppDataJson(final long durationInNanos, final long contentLength) {
        final String crossProcessId = this.tx.getCrossProcessConfig().getCrossProcessId();
        final String transactionName = this.tx.getPriorityTransactionName().getName();
        final Float queueTimeInSeconds = this.tx.getExternalTime() / 1000.0f;
        final Float durationInSeconds = durationInNanos / 1.0E9f;
        final List<?> args = Arrays.asList(crossProcessId, transactionName, queueTimeInSeconds, durationInSeconds, contentLength, this.tx.getGuid());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(out);
        try {
            JSONArray.writeJSONString(args, writer);
            writer.close();
            return out.toString();
        }
        catch (IOException e) {
            final String msg = MessageFormat.format("Error getting JSON: {0}", e);
            Agent.LOG.error(msg);
            return null;
        }
    }
    
    private void recordClientApplicationMetric(final long durationInNanos) {
        if (this.tx.getInboundHeaderState().isTrustedCatRequest()) {
            final String metricName = MessageFormat.format("ClientApplication/{0}/all", this.tx.getInboundHeaderState().getClientCrossProcessId());
            this.tx.getTransactionActivity().getTransactionStats().getUnscopedStats().getResponseTimeStats(metricName).recordResponseTime(durationInNanos, TimeUnit.NANOSECONDS);
        }
    }
    
    private boolean getForceTransactionTrace() {
        return false;
    }
    
    public String getTripId() {
        if (this.tripId == null) {
            this.tripId = this.tx.getInboundHeaderState().getInboundTripId();
        }
        if (this.tripId == null && this.isCatOriginator) {
            this.tripId = this.tx.getGuid();
        }
        return this.tripId;
    }
    
    public int generatePathHash() {
        synchronized (this.lock) {
            final int pathHash = ServiceUtils.calculatePathHash(this.tx.getApplicationName(), this.tx.getPriorityTransactionName().getName(), this.tx.getInboundHeaderState().getReferringPathHash());
            if (this.alternatePathHashes.size() < 10) {
                this.alternatePathHashes.add(ServiceUtils.intToHexString(pathHash));
            }
            return pathHash;
        }
    }
    
    public String getAlternatePathHashes() {
        synchronized (this.lock) {
            final Set<String> hashes = new TreeSet<String>(this.alternatePathHashes);
            hashes.remove(ServiceUtils.intToHexString(this.generatePathHash()));
            final StringBuilder result = new StringBuilder();
            for (final String alternatePathHash : hashes) {
                result.append(alternatePathHash);
                result.append(",");
            }
            return (result.length() > 0) ? result.substring(0, result.length() - 1) : null;
        }
    }
    
    public static CrossProcessTransactionStateImpl create(final ITransaction tx) {
        return (tx == null) ? null : new CrossProcessTransactionStateImpl(tx);
    }
    
    public String getRequestMetadata() {
        final OutboundHeadersMap metadata = new OutboundHeadersMap(HeaderType.MESSAGE);
        this.populateRequestMetadata((OutboundHeaders)metadata);
        if (metadata.isEmpty()) {
            return null;
        }
        final String serializedMetadata = JSONValue.toJSONString(metadata);
        final String encodingKey = this.tx.getCrossProcessConfig().getEncodingKey();
        try {
            return Obfuscator.obfuscateNameUsingKey(serializedMetadata, encodingKey);
        }
        catch (UnsupportedEncodingException e) {
            Agent.LOG.log(Level.FINEST, "Error encoding metadata {0} using key {1}: {2}", new Object[] { serializedMetadata, encodingKey, e });
            return null;
        }
    }
    
    public void processRequestMetadata(final String requestMetadata) {
        final InboundHeaders headers = this.decodeMetadata(requestMetadata);
        Transaction.getTransaction().provideRawHeaders(headers);
    }
    
    public String getResponseMetadata() {
        final OutboundHeadersMap metadata = new OutboundHeadersMap(HeaderType.MESSAGE);
        this.populateResponseMetadata((OutboundHeaders)metadata, -1L);
        if (metadata.isEmpty()) {
            return null;
        }
        final String serializedMetadata = JSONValue.toJSONString(metadata);
        final String encodingKey = this.tx.getCrossProcessConfig().getEncodingKey();
        try {
            return Obfuscator.obfuscateNameUsingKey(serializedMetadata, encodingKey);
        }
        catch (UnsupportedEncodingException e) {
            Agent.LOG.log(Level.SEVERE, "Error encoding metadata {0} using key {1}: {2}", new Object[] { serializedMetadata, encodingKey, e });
            return null;
        }
    }
    
    public void processResponseMetadata(final String responseMetadata) {
        if (!this.tx.getCrossProcessConfig().isCrossApplicationTracing()) {
            return;
        }
        if (responseMetadata == null) {
            return;
        }
        final Tracer lastTracer = this.tx.getTransactionActivity().getLastTracer();
        if (lastTracer == null) {
            return;
        }
        if (lastTracer instanceof AbstractExternalComponentTracer) {
            final AbstractExternalComponentTracer externalTracer = (AbstractExternalComponentTracer)lastTracer;
            final String host = externalTracer.getHost();
            final String uri = this.tx.getDispatcher().getUri();
            final InboundHeaders NRHeaders = this.decodeMetadata(responseMetadata);
            if (NRHeaders != null) {
                final String decodedAppData = HeadersUtil.getAppDataHeader(NRHeaders);
                final CrossProcessNameFormat crossProcessFormat = CrossProcessNameFormat.create(host, uri, decodedAppData);
                this.doProcessInboundResponseHeaders((TracedMethod)lastTracer, crossProcessFormat, host, true);
            }
        }
    }
    
    private InboundHeaders decodeMetadata(final String metadata) {
        String deobfuscatedMetadata;
        try {
            final String encodingKey = this.tx.getCrossProcessConfig().getEncodingKey();
            if (encodingKey == null) {
                return null;
            }
            deobfuscatedMetadata = Obfuscator.deobfuscateNameUsingKey(metadata, encodingKey);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        final Object obj = JSONValue.parse(deobfuscatedMetadata);
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Map)) {
            return null;
        }
        final Map<Object, Object> delegate = (Map<Object, Object>)obj;
        return (InboundHeaders)new InboundHeaders() {
            public HeaderType getHeaderType() {
                return HeaderType.MESSAGE;
            }
            
            public String getHeader(final String name) {
                if (delegate.containsKey(name)) {
                    return delegate.get(name).toString();
                }
                return null;
            }
        };
    }
}
