// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.util.CleverClassLoader;
import javax.management.Attribute;
import javax.management.ObjectInstance;
import com.newrelic.agent.stats.StatsEngine;
import java.util.HashMap;
import java.util.Map;

public class JmxAttributeProcessorWrapper implements JmxAttributeProcessor
{
    private static final int MAX_SIZE = 100;
    private final JmxAttributeProcessor JMX_ATTRIBUTE_PROCESSOR_NONE;
    private final String jmxAttributeProcessorClassName;
    private final Map<ClassLoader, JmxAttributeProcessor> jmxAttributeProcessorClasses;
    
    private JmxAttributeProcessorWrapper(final String jmxAttributeProcessorClassName) {
        this.JMX_ATTRIBUTE_PROCESSOR_NONE = new JmxAttributeProcessorNone();
        this.jmxAttributeProcessorClasses = new HashMap<ClassLoader, JmxAttributeProcessor>();
        this.jmxAttributeProcessorClassName = jmxAttributeProcessorClassName;
    }
    
    public boolean process(final StatsEngine statsEngine, final ObjectInstance instance, final Attribute attribute, final String metricName, final Map<String, Float> values) {
        final Object value = attribute.getValue();
        if (value == null || value instanceof Number || value instanceof String || value instanceof Boolean) {
            return false;
        }
        final JmxAttributeProcessor processor = this.getJmxAttributeProcessor(value);
        return processor != null && processor.process(statsEngine, instance, attribute, metricName, values);
    }
    
    private JmxAttributeProcessor getJmxAttributeProcessor(final Object attributeValue) {
        ClassLoader cl = attributeValue.getClass().getClassLoader();
        cl = ((cl == null) ? ClassLoader.getSystemClassLoader() : cl);
        JmxAttributeProcessor processor = this.jmxAttributeProcessorClasses.get(cl);
        if (processor == null) {
            try {
                final CleverClassLoader classLoader = new CleverClassLoader(cl);
                processor = classLoader.loadClassSpecial(this.jmxAttributeProcessorClassName).newInstance();
                if (this.jmxAttributeProcessorClasses.size() > 100) {
                    this.jmxAttributeProcessorClasses.clear();
                }
                this.jmxAttributeProcessorClasses.put(cl, processor);
                if (Agent.LOG.isLoggable(Level.FINER)) {
                    final String msg = MessageFormat.format("Loaded {0} using class loader {1}", this.jmxAttributeProcessorClassName, cl);
                    Agent.LOG.finer(msg);
                }
            }
            catch (Throwable t) {
                this.jmxAttributeProcessorClasses.put(cl, this.JMX_ATTRIBUTE_PROCESSOR_NONE);
                final String msg = MessageFormat.format("Error loading {0} using class loader {1}: {2}", this.jmxAttributeProcessorClassName, cl, t.toString());
                if (Agent.LOG.isLoggable(Level.FINEST)) {
                    Agent.LOG.log(Level.FINEST, msg, t);
                }
                else {
                    Agent.LOG.finer(msg);
                }
            }
        }
        return processor;
    }
    
    protected static JmxAttributeProcessor createInstance(final String jmxAttributeProcessorClassName) {
        return new JmxAttributeProcessorWrapper(jmxAttributeProcessorClassName);
    }
    
    private static class JmxAttributeProcessorNone implements JmxAttributeProcessor
    {
        public boolean process(final StatsEngine statsEngine, final ObjectInstance instance, final Attribute attribute, final String metricName, final Map<String, Float> values) {
            return false;
        }
    }
}
