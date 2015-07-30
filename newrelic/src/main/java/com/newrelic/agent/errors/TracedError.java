// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import java.io.Writer;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.attributes.AttributesUtils;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public abstract class TracedError implements Comparable<TracedError>, JSONStreamAware
{
    private final String path;
    private final long timestamp;
    private final String requestUri;
    private final String appName;
    private final Map<String, Map<String, String>> prefixAtts;
    private final Map<String, Object> userAtts;
    private final Map<String, Object> agentAtts;
    private final Map<String, String> errorAtts;
    private final Map<String, Object> intrinsics;
    
    public TracedError(final String appName, final String frontendMetricName, final String requestPath, final long timestamp, final Map<String, Map<String, String>> prefixedParams, final Map<String, Object> userParams, final Map<String, Object> agentParams, final Map<String, String> errorParams, final Map<String, Object> intrinsics) {
        this.appName = appName;
        this.path = ((frontendMetricName == null) ? "Unknown" : frontendMetricName);
        this.requestUri = ((requestPath == null) ? "Unknown" : requestPath);
        this.timestamp = timestamp;
        this.prefixAtts = this.setAtts(prefixedParams);
        this.userAtts = this.setAtts(userParams);
        this.agentAtts = this.setAtts(agentParams);
        this.errorAtts = this.setAtts(errorParams);
        this.intrinsics = this.setAtts(intrinsics);
    }
    
    private <V, K> Map<K, V> setAtts(final Map<K, V> inputAtts) {
        if (inputAtts == null) {
            return Collections.emptyMap();
        }
        return inputAtts;
    }
    
    public abstract String getMessage();
    
    public abstract String getExceptionClass();
    
    public long getTimestamp() {
        return this.timestamp / 1000L;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public abstract Collection<String> stackTrace();
    
    public Map<String, Collection<String>> stackTraces() {
        return Collections.emptyMap();
    }
    
    private Map<String, Object> getUserAtts() {
        final Map<String, Object> atts = (Map<String, Object>)Maps.newHashMap();
        atts.putAll(this.errorAtts);
        atts.putAll(this.userAtts);
        return atts;
    }
    
    private Map<String, Object> getAgentAtts() {
        final Map<String, Object> atts = (Map<String, Object>)Maps.newHashMap();
        atts.putAll(this.agentAtts);
        if (this.prefixAtts != null && !this.prefixAtts.isEmpty()) {
            atts.putAll(AttributesUtils.appendAttributePrefixes(this.prefixAtts));
        }
        return atts;
    }
    
    private void filterAndAddIfNotEmpty(final String key, final Map<String, Object> wheretoAdd, final Map<String, ?> toAdd) {
        final Map<String, ?> output = ServiceFactory.getAttributesService().filterErrorAttributes(this.appName, toAdd);
        if (output != null && !output.isEmpty()) {
            wheretoAdd.put(key, output);
        }
    }
    
    private Map<String, Object> getAttributes() {
        final Map<String, Object> params = (Map<String, Object>)Maps.newHashMap();
        if (ServiceFactory.getAttributesService().isAttributesEnabledForErrors(this.appName)) {
            this.filterAndAddIfNotEmpty("agentAttributes", params, this.getAgentAtts());
            if (!ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity()) {
                this.filterAndAddIfNotEmpty("userAttributes", params, this.getUserAtts());
            }
        }
        if (this.intrinsics != null && !this.intrinsics.isEmpty()) {
            params.put("intrinsics", this.intrinsics);
        }
        final Collection<String> stackTrace = this.stackTrace();
        if (stackTrace != null) {
            params.put("stack_trace", stackTrace);
        }
        else {
            final Map<String, Collection<String>> stackTraces = this.stackTraces();
            if (stackTraces != null) {
                params.put("stack_traces", stackTraces);
            }
        }
        params.put("request_uri", this.requestUri);
        return params;
    }
    
    public void writeJSONString(final Writer writer) throws IOException {
        JSONArray.writeJSONString(Arrays.asList(this.getTimestamp(), this.getPath(), this.getMessage(), this.getExceptionClass(), this.getAttributes()), writer);
    }
    
    public int compareTo(final TracedError other) {
        return (int)(this.timestamp - other.timestamp);
    }
    
    public boolean incrementsErrorMetric() {
        return true;
    }
}
