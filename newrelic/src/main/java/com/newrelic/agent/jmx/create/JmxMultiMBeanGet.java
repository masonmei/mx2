// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import java.util.Iterator;
import java.util.HashMap;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import javax.management.MalformedObjectNameException;
import com.newrelic.agent.extension.Extension;
import java.util.List;
import com.newrelic.agent.jmx.JmxType;
import java.util.Map;

public class JmxMultiMBeanGet extends JmxGet
{
    public JmxMultiMBeanGet(final String pObjectName, final String rootMetricName, final String safeName, final Map<JmxType, List<String>> pAttributesToType, final Extension origin) throws MalformedObjectNameException {
        super(pObjectName, rootMetricName, safeName, pAttributesToType, origin);
    }
    
    public JmxMultiMBeanGet(final String pObjectName, final String safeName, final String pRootMetric, final List<JmxMetric> pMetrics, final JmxAttributeFilter attributeFilter, final JmxMetricModifier modifier) throws MalformedObjectNameException {
        super(pObjectName, safeName, pRootMetric, pMetrics, attributeFilter, modifier);
    }
    
    public void recordStats(final StatsEngine statsEngine, final Map<ObjectName, Map<String, Float>> resultingMetricToValue, final MBeanServer server) {
        final Map<ObjectName, String> rootMetricNames = new HashMap<ObjectName, String>();
        for (final JmxMetric currentMetric : this.getJmxMetrics()) {
            final Map<String, Float> mbeansWithValues = new HashMap<String, Float>();
            for (final Map.Entry<ObjectName, Map<String, Float>> currentMBean : resultingMetricToValue.entrySet()) {
                String actualRootMetricName = rootMetricNames.get(currentMBean.getKey());
                if (actualRootMetricName == null) {
                    actualRootMetricName = this.getRootMetricName(currentMBean.getKey(), server);
                }
                currentMetric.applySingleMBean(actualRootMetricName, currentMBean.getValue(), mbeansWithValues);
            }
            currentMetric.recordMultMBeanStats(statsEngine, mbeansWithValues);
        }
    }
}
