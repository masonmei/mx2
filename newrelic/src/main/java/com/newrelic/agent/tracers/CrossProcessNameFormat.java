// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import java.io.UnsupportedEncodingException;
import com.newrelic.agent.util.Obfuscator;
import com.newrelic.agent.deps.org.json.simple.parser.ParseException;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import com.newrelic.agent.deps.org.json.simple.parser.JSONParser;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class CrossProcessNameFormat implements MetricNameFormat
{
    private final String transactionName;
    private final String crossProcessId;
    private final String hostName;
    private final String uri;
    private final String transactionId;
    
    private CrossProcessNameFormat(final String transactionName, final String crossProcessId, final String hostName, final String uri, final String transactionId) {
        this.hostName = hostName;
        this.crossProcessId = crossProcessId;
        this.transactionName = transactionName;
        this.uri = uri;
        this.transactionId = transactionId;
    }
    
    public String getHostCrossProcessIdRollupMetricName() {
        return Strings.join('/', "ExternalApp", this.hostName, this.crossProcessId, "all");
    }
    
    public String getTransactionId() {
        return this.transactionId;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("host:").append(this.hostName).append(" crossProcessId:").append(this.crossProcessId).append(" transactionName:").append(this.transactionName).append(" uri:").append(this.uri).append(" transactionId:").append(this.transactionId);
        return sb.toString();
    }
    
    public String getMetricName() {
        return Strings.join('/', "ExternalTransaction", this.hostName, this.crossProcessId, this.transactionName);
    }
    
    public String getTransactionSegmentName() {
        return this.getMetricName();
    }
    
    public String getTransactionSegmentUri() {
        return this.uri;
    }
    
    public static CrossProcessNameFormat create(final String host, final String uri, final String decodedAppData) {
        if (decodedAppData == null) {
            return null;
        }
        if (host == null || host.length() == 0) {
            return null;
        }
        try {
            final JSONParser parser = new JSONParser();
            final JSONArray arr = (JSONArray)parser.parse(decodedAppData);
            final String crossProcessId = arr.get(0);
            final String transactionName = arr.get(1);
            String transactionId = null;
            if (arr.size() > 5) {
                transactionId = arr.get(5);
            }
            return new CrossProcessNameFormat(transactionName, crossProcessId, host, uri, transactionId);
        }
        catch (ParseException ex) {
            if (Agent.LOG.isFinerEnabled()) {
                final String msg = MessageFormat.format("Unable to parse application data {0}: {1}", decodedAppData, ex);
                Agent.LOG.finer(msg);
            }
            return null;
        }
    }
    
    public static CrossProcessNameFormat create(final String host, final String uri, final String encodedAppData, final String encodingKey) {
        if (encodedAppData == null) {
            return null;
        }
        if (encodingKey == null) {
            return null;
        }
        if (host == null || host.length() == 0) {
            return null;
        }
        String decodedAppData = null;
        try {
            decodedAppData = Obfuscator.deobfuscateNameUsingKey(encodedAppData, encodingKey);
        }
        catch (UnsupportedEncodingException ex) {
            final String msg = MessageFormat.format("Error decoding application data {0}: {1}", encodedAppData, ex);
            Agent.LOG.error(msg);
            return null;
        }
        return create(host, uri, decodedAppData);
    }
}
