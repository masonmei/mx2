// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.metricname;

import java.util.regex.Matcher;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.util.Strings;
import java.util.regex.Pattern;

public class MetricNameFormats
{
    private static final Pattern METRIC_NAME_REPLACE;
    
    public static MetricNameFormat replaceFirstSegment(final MetricNameFormat metricName, final String newSegmentName) {
        final String metricNameString = metricName.getMetricName();
        final String txName = metricName.getTransactionSegmentName();
        final String newMetricName = replaceFirstSegment(metricNameString, newSegmentName);
        String newTxName;
        if (metricNameString.equals(txName)) {
            newTxName = newMetricName;
        }
        else {
            newTxName = replaceFirstSegment(txName, newSegmentName);
        }
        return new SimpleMetricNameFormat(newMetricName, newTxName);
    }
    
    private static String replaceFirstSegment(final String name, final String newSegmentName) {
        final String[] segments = name.split("/");
        segments[0] = newSegmentName;
        return Strings.join('/', segments);
    }
    
    public static MetricNameFormat getFormatter(final Object invocationTarget, final ClassMethodSignature sig, final String metricName, final int flags) {
        if (null == metricName) {
            return sig.getMetricNameFormat(invocationTarget, flags);
        }
        return new SimpleMetricNameFormat(getTracerMetricName(invocationTarget, sig.getClassName(), metricName));
    }
    
    private static String getTracerMetricName(final Object invocationTarget, final String className, final String metricName) {
        final Matcher matcher = MetricNameFormats.METRIC_NAME_REPLACE.matcher(metricName);
        return matcher.replaceFirst(Matcher.quoteReplacement((invocationTarget == null) ? className : invocationTarget.getClass().getName()));
    }
    
    static {
        METRIC_NAME_REPLACE = Pattern.compile("${className}", 16);
    }
}
