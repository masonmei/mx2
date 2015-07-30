// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.yaml;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.ClassMethodSignature;

public interface MetricNameFormatFactory
{
    MetricNameFormat getMetricNameFormat(ClassMethodSignature p0, Object p1, Object[] p2);
}
