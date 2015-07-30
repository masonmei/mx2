// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import java.util.Iterator;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.jmx.JmxType;

public abstract class JmxMetric
{
    private final String attributeMetricName;
    private final String[] attributes;
    private final JmxAction action;
    
    protected JmxMetric(final String pAttribute) {
        this(new String[] { pAttribute }, null, JmxAction.USE_FIRST_ATT);
    }
    
    protected JmxMetric(final String[] pAttributes, final String pAttMetricName, final JmxAction pAction) throws IllegalArgumentException {
        this.attributes = pAttributes;
        if (pAttMetricName == null) {
            this.attributeMetricName = pAttributes[0];
        }
        else {
            this.attributeMetricName = pAttMetricName;
        }
        this.action = pAction;
    }
    
    public static JmxMetric create(final String attribute, final JmxType type) {
        if (JmxType.MONOTONICALLY_INCREASING.equals(type)) {
            return new MonotonicallyIncreasingJmxMetric(attribute);
        }
        return new SimpleJmxMetric(attribute);
    }
    
    public static JmxMetric create(final String attribute, final String attMetricName, final JmxType type) {
        if (JmxType.MONOTONICALLY_INCREASING.equals(type)) {
            return new MonotonicallyIncreasingJmxMetric(new String[] { attribute }, attMetricName, JmxAction.USE_FIRST_ATT);
        }
        return new SimpleJmxMetric(new String[] { attribute }, attMetricName, JmxAction.USE_FIRST_ATT);
    }
    
    public static JmxMetric create(final String[] attributes, final String attMetricName, final JmxAction pAction, final JmxType type) {
        if (attributes == null || attributes.length == 0) {
            throw new IllegalArgumentException("A JmxMetric can not be created with zero attributes.");
        }
        if (JmxType.MONOTONICALLY_INCREASING.equals(type)) {
            return new MonotonicallyIncreasingJmxMetric(attributes, attMetricName, pAction);
        }
        return new SimpleJmxMetric(attributes, attMetricName, pAction);
    }
    
    public abstract void recordStats(final StatsEngine p0, final String p1, final float p2);
    
    public abstract JmxType getType();
    
    public String getAttributeMetricName() {
        return this.attributeMetricName;
    }
    
    public String[] getAttributes() {
        return this.attributes;
    }
    
    public void applySingleMBean(final String rootMetricName, final Map<String, Float> inputAttToValues, final Map<String, Float> resultingValues) {
        final String fullMetricName = rootMetricName + this.attributeMetricName;
        try {
            float value = this.action.performAction(this.attributes, inputAttToValues);
            final Float oldVal = resultingValues.get(fullMetricName);
            if (oldVal != null) {
                value += oldVal;
            }
            resultingValues.put(fullMetricName, value);
            Agent.LOG.log(Level.FINER, "Adding Multi Bean: {0} Value: {1}", new Object[] { fullMetricName, value });
        }
        catch (IllegalArgumentException e) {
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, MessageFormat.format("JMX Metric {0} not recorded. {1}", fullMetricName, e.getMessage()));
            }
        }
    }
    
    public void recordMultMBeanStats(final StatsEngine statsEngine, final Map<String, Float> metricWithValues) {
        for (final Map.Entry<String, Float> current : metricWithValues.entrySet()) {
            if (current.getKey().length() > 0) {
                this.recordStats(statsEngine, current.getKey(), current.getValue());
                Agent.LOG.log(Level.FINER, "JMX Multi Bean Metric: {0} Value: {1}", new Object[] { current.getKey(), current.getValue() });
            }
        }
    }
    
    public void recordSingleMBeanStats(final StatsEngine statsEngine, final String rootMetricName, final Map<String, Float> values) {
        final String fullMetricName = rootMetricName + this.attributeMetricName;
        try {
            final float val = this.action.performAction(this.attributes, values);
            this.recordStats(statsEngine, fullMetricName, val);
            Agent.LOG.log(Level.FINER, "JMX Metric: {0} Value: {1}", new Object[] { fullMetricName, val });
        }
        catch (IllegalArgumentException e) {
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, MessageFormat.format("JMX Metric {0} not recorded. {1}", fullMetricName, e.getMessage()));
            }
        }
    }
}
