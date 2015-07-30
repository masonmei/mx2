// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import java.util.concurrent.TimeUnit;

public abstract class AbstractStatsAttributeProcessor implements JmxAttributeProcessor
{
    protected static boolean isBuiltInMetric(final String metricName) {
        return metricName != null && metricName.startsWith("JmxBuiltIn");
    }
    
    protected static TimeUnit getTimeUnit(final String unit) {
        if ("HOUR".equals(unit)) {
            return TimeUnit.HOURS;
        }
        if ("MINUTE".equals(unit)) {
            return TimeUnit.MINUTES;
        }
        if ("SECOND".equals(unit)) {
            return TimeUnit.SECONDS;
        }
        if ("MILLISECOND".equals(unit)) {
            return TimeUnit.MILLISECONDS;
        }
        if ("MICROSECOND".equals(unit)) {
            return TimeUnit.MICROSECONDS;
        }
        if ("NANOSECOND".equals(unit)) {
            return TimeUnit.NANOSECONDS;
        }
        return TimeUnit.MILLISECONDS;
    }
}
