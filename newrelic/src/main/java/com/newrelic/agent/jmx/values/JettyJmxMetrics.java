// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.JmxType;
import com.newrelic.agent.jmx.metrics.JmxAction;
import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class JettyJmxMetrics extends JmxFrameworkValues
{
    private static String PREFIX;
    private static final int METRIC_COUNT = 1;
    private static List<BaseJmxValue> METRICS;
    private static final JmxMetric CURRENT_MAX_COUNT;
    private static final JmxMetric CURRENT_IDLE_COUNT;
    private static final JmxMetric CURRENT_ACTIVE_COUNT;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return JettyJmxMetrics.METRICS;
    }
    
    public String getPrefix() {
        return JettyJmxMetrics.PREFIX;
    }
    
    static {
        JettyJmxMetrics.PREFIX = "org.eclipse.jetty";
        JettyJmxMetrics.METRICS = new ArrayList<BaseJmxValue>(1);
        CURRENT_MAX_COUNT = ServerJmxMetricGenerator.MAX_THREAD_POOL_COUNT.createMetric("maxThreads");
        CURRENT_IDLE_COUNT = ServerJmxMetricGenerator.IDLE_THREAD_POOL_COUNT.createMetric("idleThreads");
        CURRENT_ACTIVE_COUNT = JmxMetric.create(new String[] { "threads", "idleThreads" }, "Active", JmxAction.SUBTRACT_ALL_FROM_FIRST, JmxType.SIMPLE);
        JettyJmxMetrics.METRICS.add(new BaseJmxValue("org.eclipse.jetty.util.thread:type=queuedthreadpool,id=*", "JmxBuiltIn/ThreadPool/{id}/", new JmxMetric[] { JettyJmxMetrics.CURRENT_IDLE_COUNT, JettyJmxMetrics.CURRENT_ACTIVE_COUNT, JettyJmxMetrics.CURRENT_MAX_COUNT }));
    }
}
