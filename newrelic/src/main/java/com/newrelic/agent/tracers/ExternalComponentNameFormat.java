// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.util.Strings;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class ExternalComponentNameFormat implements MetricNameFormat
{
    private String metricName;
    private String transactionSegmentName;
    private final String[] operations;
    private final boolean includeOperationInMetric;
    private final String host;
    private final String library;
    private final String transactionSegmentUri;
    
    public ExternalComponentNameFormat(final String host, final String library, final boolean includeOperationInMetric, final String pTransactionSegmentUri, final String[] operations) {
        this.host = host;
        this.library = library;
        this.operations = operations;
        this.includeOperationInMetric = includeOperationInMetric;
        this.transactionSegmentUri = pTransactionSegmentUri;
        this.setMetricName();
    }
    
    public ExternalComponentNameFormat cloneWithNewHost(final String hostName) {
        return new ExternalComponentNameFormat(hostName, this.library, this.includeOperationInMetric, this.transactionSegmentUri, this.operations);
    }
    
    private void setMetricName() {
        this.metricName = Strings.join('/', "External", this.host, this.library);
        if (this.includeOperationInMetric) {
            this.metricName += this.fixOperations(this.operations);
            this.transactionSegmentName = this.metricName;
        }
    }
    
    public String getMetricName() {
        return this.metricName;
    }
    
    public String getTransactionSegmentName() {
        if (this.transactionSegmentName == null) {
            this.transactionSegmentName = this.metricName + this.fixOperations(this.operations);
        }
        return this.transactionSegmentName;
    }
    
    private String fixOperations(final String[] operations) {
        final StringBuilder builder = new StringBuilder();
        for (final String operation : operations) {
            if (operation.startsWith("/")) {
                builder.append(operation);
            }
            else {
                builder.append('/').append(operation);
            }
        }
        return builder.toString();
    }
    
    public String getTransactionSegmentUri() {
        return this.transactionSegmentUri;
    }
    
    public static MetricNameFormat create(final String host, final String library, final boolean includeOperationInMetric, final String uri, final String... operations) {
        return new ExternalComponentNameFormat(host, library, includeOperationInMetric, uri, operations);
    }
}
