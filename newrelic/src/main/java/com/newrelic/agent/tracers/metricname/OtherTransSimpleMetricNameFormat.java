// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.metricname;

public class OtherTransSimpleMetricNameFormat implements MetricNameFormat
{
    private final String metricName;
    private final String transactionSegmentName;
    
    public OtherTransSimpleMetricNameFormat(final String metricName) {
        final String appendOtherTrans = appendOtherTrans(metricName);
        this.transactionSegmentName = appendOtherTrans;
        this.metricName = appendOtherTrans;
    }
    
    public OtherTransSimpleMetricNameFormat(final String metricName, final String transactionSegmentName) {
        this.metricName = appendOtherTrans(metricName);
        this.transactionSegmentName = transactionSegmentName;
    }
    
    private static String appendOtherTrans(final String pMetricName) {
        if (pMetricName != null) {
            final StringBuilder sb = new StringBuilder();
            if (!pMetricName.startsWith("OtherTransaction/")) {
                sb.append("OtherTransaction");
                if (!pMetricName.startsWith("/")) {
                    sb.append("/");
                }
            }
            sb.append(pMetricName);
            return sb.toString();
        }
        return pMetricName;
    }
    
    public final String getMetricName() {
        return this.metricName;
    }
    
    public String getTransactionSegmentName() {
        return this.transactionSegmentName;
    }
    
    public String getTransactionSegmentUri() {
        return null;
    }
}
