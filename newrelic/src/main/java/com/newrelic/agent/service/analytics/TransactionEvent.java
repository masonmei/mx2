// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import java.util.Collections;
import com.newrelic.agent.service.ServiceFactory;
import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import com.newrelic.agent.service.ServiceUtils;
import com.newrelic.agent.deps.org.json.simple.JSONObject;
import java.io.Writer;
import java.util.Map;
import com.newrelic.agent.stats.ApdexPerfZone;

public class TransactionEvent extends AnalyticsEvent
{
    static final float UNASSIGNED = Float.NEGATIVE_INFINITY;
    static final int UNASSIGNED_INT = Integer.MIN_VALUE;
    static final String TYPE = "Transaction";
    final String guid;
    final String referrerGuid;
    final String tripId;
    final Integer referringPathHash;
    Integer pathHash;
    final String alternatePathHashes;
    final ApdexPerfZone apdexPerfZone;
    final String syntheticsResourceId;
    final String syntheticsMonitorId;
    final String syntheticsJobId;
    final int port;
    final String name;
    final float duration;
    float queueDuration;
    float externalDuration;
    float externalCallCount;
    float databaseDuration;
    float databaseCallCount;
    float gcCumulative;
    Map<String, Object> agentAttributes;
    String appName;
    
    public TransactionEvent(final String appName, final String subType, final long timestamp, final String name, final float duration, final String guid, final String referringGuid, final Integer port, final String tripId, final Integer referringPathHash, final String alternatePathHashes, final ApdexPerfZone apdexPerfZone, final String syntheticsResourceId, final String syntheticsMonitorId, final String syntheticsJobId) {
        super("Transaction", timestamp);
        this.queueDuration = Float.NEGATIVE_INFINITY;
        this.externalDuration = Float.NEGATIVE_INFINITY;
        this.externalCallCount = Float.NEGATIVE_INFINITY;
        this.databaseDuration = Float.NEGATIVE_INFINITY;
        this.databaseCallCount = Float.NEGATIVE_INFINITY;
        this.gcCumulative = Float.NEGATIVE_INFINITY;
        this.name = name;
        this.duration = duration;
        this.guid = guid;
        this.referrerGuid = referringGuid;
        this.tripId = tripId;
        this.referringPathHash = referringPathHash;
        this.alternatePathHashes = alternatePathHashes;
        this.port = ((port == null) ? Integer.MIN_VALUE : port);
        this.appName = appName;
        this.apdexPerfZone = apdexPerfZone;
        this.syntheticsResourceId = syntheticsResourceId;
        this.syntheticsMonitorId = syntheticsMonitorId;
        this.syntheticsJobId = syntheticsJobId;
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        final JSONObject obj = new JSONObject();
        obj.put("type", this.type);
        obj.put("timestamp", this.timestamp);
        obj.put("name", this.name);
        obj.put("duration", this.duration);
        if (this.apdexPerfZone != null) {
            obj.put("apdexPerfZone", this.apdexPerfZone.getZone());
        }
        if (this.guid != null) {
            obj.put("nr.guid", this.guid);
        }
        if (this.referrerGuid != null) {
            obj.put("nr.referringTransactionGuid", this.referrerGuid);
        }
        if (this.tripId != null) {
            obj.put("nr.tripId", this.tripId);
        }
        if (this.pathHash != null) {
            obj.put("nr.pathHash", ServiceUtils.intToHexString(this.pathHash));
        }
        if (this.referringPathHash != null) {
            obj.put("nr.referringPathHash", ServiceUtils.intToHexString(this.referringPathHash));
        }
        if (this.alternatePathHashes != null) {
            obj.put("nr.alternatePathHashes", this.alternatePathHashes);
        }
        if (this.syntheticsResourceId != null) {
            obj.put("nr.syntheticsResourceId", this.syntheticsResourceId);
        }
        if (this.syntheticsMonitorId != null) {
            obj.put("nr.syntheticsMonitorId", this.syntheticsMonitorId);
        }
        if (this.syntheticsJobId != null) {
            obj.put("nr.syntheticsJobId", this.syntheticsJobId);
        }
        if (this.port != Integer.MIN_VALUE) {
            obj.put("port", this.port);
        }
        if (this.queueDuration != Float.NEGATIVE_INFINITY) {
            obj.put("queueDuration", this.queueDuration);
        }
        if (this.externalDuration != Float.NEGATIVE_INFINITY) {
            obj.put("externalDuration", this.externalDuration);
        }
        if (this.externalCallCount > 0.0f) {
            obj.put("externalCallCount", this.externalCallCount);
        }
        if (this.databaseDuration != Float.NEGATIVE_INFINITY) {
            obj.put("databaseDuration", this.databaseDuration);
        }
        if (this.databaseCallCount > 0.0f) {
            obj.put("databaseCallCount", this.databaseCallCount);
        }
        if (this.gcCumulative != Float.NEGATIVE_INFINITY) {
            obj.put("gcCumulative", this.gcCumulative);
        }
        final Map<String, ?> filteredUserAtts = this.getUserFilteredMap(this.userAttributes);
        final Map<String, ?> filteredAgentAtts = this.getFilteredMap(this.agentAttributes);
        if (filteredAgentAtts.isEmpty()) {
            if (filteredUserAtts.isEmpty()) {
                JSONArray.writeJSONString(Arrays.asList(obj), out);
            }
            else {
                JSONArray.writeJSONString(Arrays.asList(obj, filteredUserAtts), out);
            }
        }
        else {
            JSONArray.writeJSONString(Arrays.asList(obj, filteredUserAtts, filteredAgentAtts), out);
        }
    }
    
    private Map<String, ?> getFilteredMap(final Map<String, Object> input) {
        return ServiceFactory.getAttributesService().filterEventAttributes(this.appName, input);
    }
    
    private Map<String, ?> getUserFilteredMap(final Map<String, Object> input) {
        if (!ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity()) {
            return this.getFilteredMap(input);
        }
        return Collections.emptyMap();
    }
    
    public boolean isValid() {
        return true;
    }
}
