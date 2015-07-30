// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import java.util.Arrays;
import com.newrelic.agent.jmx.create.JmxMetricModifier;
import com.newrelic.agent.jmx.create.JmxAttributeFilter;
import java.util.List;

public class BaseJmxValue
{
    private final String objectNameString;
    private final String objectMetricName;
    private final List<JmxMetric> metrics;
    private final JmxAttributeFilter attributeFilter;
    private final JMXMetricType type;
    private final JmxMetricModifier modifier;
    
    public BaseJmxValue(final String pObjectName, final String pObjectMetricName, final JmxMetric[] pMetrics) {
        this(pObjectName, pObjectMetricName, null, null, JMXMetricType.INCREMENT_COUNT_PER_BEAN, pMetrics);
    }
    
    public BaseJmxValue(final String pObjectName, final String pObjectMetricName, final JmxAttributeFilter attributeFilter, final JmxMetric[] pMetrics) {
        this(pObjectName, pObjectMetricName, attributeFilter, null, JMXMetricType.INCREMENT_COUNT_PER_BEAN, pMetrics);
    }
    
    public BaseJmxValue(final String pObjectName, final String pObjectMetricName, final JmxMetricModifier pModifier, final JmxMetric[] pMetrics) {
        this(pObjectName, pObjectMetricName, null, pModifier, JMXMetricType.INCREMENT_COUNT_PER_BEAN, pMetrics);
    }
    
    public BaseJmxValue(final String pObjectName, final String pObjectMetricName, final JmxAttributeFilter attributeFilter, final JmxMetricModifier pModifier, final JMXMetricType pType, final JmxMetric[] pMetrics) {
        this.objectNameString = pObjectName;
        this.objectMetricName = pObjectMetricName;
        this.metrics = Arrays.asList(pMetrics);
        this.attributeFilter = attributeFilter;
        this.type = pType;
        this.modifier = pModifier;
    }
    
    public String getObjectNameString() {
        return this.objectNameString;
    }
    
    public String getObjectMetricName() {
        return this.objectMetricName;
    }
    
    public List<JmxMetric> getMetrics() {
        return this.metrics;
    }
    
    public JmxAttributeFilter getAttributeFilter() {
        return this.attributeFilter;
    }
    
    public JMXMetricType getType() {
        return this.type;
    }
    
    public JmxMetricModifier getModifier() {
        return this.modifier;
    }
}
