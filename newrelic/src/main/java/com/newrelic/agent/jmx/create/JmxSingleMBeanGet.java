// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import javax.management.MalformedObjectNameException;
import com.newrelic.agent.extension.Extension;
import java.util.List;
import com.newrelic.agent.jmx.JmxType;
import java.util.Map;

public class JmxSingleMBeanGet extends JmxGet
{
    public JmxSingleMBeanGet(final String pObjectName, final String rootMetricName, final String safeName, final Map<JmxType, List<String>> pAttributesToType, final Extension origin) throws MalformedObjectNameException {
        super(pObjectName, rootMetricName, safeName, pAttributesToType, origin);
    }
    
    public JmxSingleMBeanGet(final String pObjectName, final String safeName, final String pRootMetric, final List<JmxMetric> pMetrics, final JmxAttributeFilter attributeFilter, final JmxMetricModifier modifier) throws MalformedObjectNameException {
        super(pObjectName, safeName, pRootMetric, pMetrics, attributeFilter, modifier);
    }
    
    public void recordStats(final StatsEngine statsEngine, final Map<ObjectName, Map<String, Float>> resultingMetricToValue, final MBeanServer server) {
        for (final Map.Entry<ObjectName, Map<String, Float>> currentMBean : resultingMetricToValue.entrySet()) {
            final String actualRootMetricName = this.getRootMetricName(currentMBean.getKey(), server);
            if (actualRootMetricName.length() > 0 && (this.getJmxAttributeFilter() == null || this.getJmxAttributeFilter().keepMetric(actualRootMetricName))) {
                for (final JmxMetric current : this.getJmxMetrics()) {
                    current.recordSingleMBeanStats(statsEngine, actualRootMetricName, currentMBean.getValue());
                }
            }
        }
    }
}
