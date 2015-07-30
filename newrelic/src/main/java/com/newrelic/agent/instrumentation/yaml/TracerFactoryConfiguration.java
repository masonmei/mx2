// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.yaml;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.OtherTransSimpleMetricNameFormat;
import java.util.Collections;
import java.util.Map;

public class TracerFactoryConfiguration
{
    private final boolean dispatcher;
    private final MetricNameFormatFactory metricNameFormatFactory;
    private final Map attributes;
    
    public TracerFactoryConfiguration(final String defaultMetricPrefix, final boolean pDispatcher, final Object metricNameFormat, final Map attributes) {
        this.attributes = Collections.unmodifiableMap((Map<?, ?>)attributes);
        this.dispatcher = pDispatcher;
        if (metricNameFormat instanceof String) {
            this.metricNameFormatFactory = new SimpleMetricNameFormatFactory(new OtherTransSimpleMetricNameFormat(metricNameFormat.toString()));
        }
        else if (null == metricNameFormat) {
            this.metricNameFormatFactory = new PointCutFactory.ClassMethodNameFormatDescriptor(defaultMetricPrefix, this.dispatcher);
        }
        else {
            if (!(metricNameFormat instanceof MetricNameFormatFactory)) {
                throw new RuntimeException("Unsupported metric_name_format value");
            }
            this.metricNameFormatFactory = (MetricNameFormatFactory)metricNameFormat;
        }
    }
    
    public Map getAttributes() {
        return this.attributes;
    }
    
    public MetricNameFormatFactory getMetricNameFormatFactory() {
        return this.metricNameFormatFactory;
    }
    
    public boolean isDispatcher() {
        return this.dispatcher;
    }
}
