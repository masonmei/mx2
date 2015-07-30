// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.yaml;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class SimpleMetricNameFormatFactory implements MetricNameFormatFactory
{
    private final MetricNameFormat metricNameFormat;
    
    public SimpleMetricNameFormatFactory(final MetricNameFormat metricNameFormat) {
        this.metricNameFormat = metricNameFormat;
    }
    
    public MetricNameFormat getMetricNameFormat(final ClassMethodSignature sig, final Object object, final Object[] args) {
        return this.metricNameFormat;
    }
}
