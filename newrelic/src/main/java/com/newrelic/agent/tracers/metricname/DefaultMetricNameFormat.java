// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.metricname;

import java.text.MessageFormat;
import com.newrelic.agent.tracers.ClassMethodSignature;

public class DefaultMetricNameFormat extends AbstractMetricNameFormat
{
    private final String metricName;
    
    public DefaultMetricNameFormat(final ClassMethodSignature sig, final Object object, final String pattern) {
        this.metricName = MessageFormat.format(pattern, object.getClass().getName(), sig.getMethodName());
    }
    
    public String getMetricName() {
        return this.metricName;
    }
}
