// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge.external;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.Transaction;

public class ExternalMetrics
{
    public static final String METRIC_NAMESPACE = "External";
    public static final String METRIC_NAME = "External/{0}/{1}";
    public static final String TRANSACTION_SEGMENT_NAME = "External/{0}/{1}/{2}";
    public static final String ALL = "External/all";
    public static final String ALL_WEB = "External/allWeb";
    public static final String ALL_OTHER = "External/allOther";
    public static final String ALL_HOST = "External/{0}/all";
    
    private static String fixOperations(final String... operations) {
        final StringBuilder builder = new StringBuilder();
        for (final String operation : operations) {
            if (operation.startsWith("/")) {
                builder.append(operation);
            }
            else {
                builder.append('/').append(operation);
            }
        }
        return builder.substring(1);
    }
    
    public static void makeExternalComponentTrace(final Transaction tx, final TracedMethod method, final String host, final String library, final boolean includeOperationInMetric, final String uri, final String... operations) {
        final String transactionSegmentName = MessageFormat.format("External/{0}/{1}/{2}", host, library, fixOperations(operations));
        final String metricName = includeOperationInMetric ? transactionSegmentName : MessageFormat.format("External/{0}/{1}", host, library);
        method.setMetricNameFormatInfo(metricName, transactionSegmentName, uri);
        method.addExclusiveRollupMetricName("External/all");
        if (tx.isWebTransaction()) {
            method.addExclusiveRollupMetricName("External/allWeb");
        }
        else {
            method.addExclusiveRollupMetricName("External/allOther");
        }
        method.addExclusiveRollupMetricName(MessageFormat.format("External/{0}/all", host));
    }
}
