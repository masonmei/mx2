// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.metrics.JtaJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class WebSphereJmxValues extends JmxFrameworkValues
{
    private static final int METRIC_COUNT = 2;
    private static final List<BaseJmxValue> METRICS;
    public static String PREFIX;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return WebSphereJmxValues.METRICS;
    }
    
    public String getPrefix() {
        return WebSphereJmxValues.PREFIX;
    }
    
    static {
        METRICS = new ArrayList<BaseJmxValue>(2);
        WebSphereJmxValues.PREFIX = "WebSphere-8";
        WebSphereJmxValues.METRICS.add(new BaseJmxValue("WebSphere:type=ThreadPool,name=*,process=*,platform=*,node=*,*", "JmxBuiltIn/ThreadPool/{name}/", new JmxMetric[] { ServerJmxMetricGenerator.ACTIVE_THREAD_POOL_COUNT.createMetric("stats.ActiveCount"), ServerJmxMetricGenerator.MAX_THREAD_POOL_COUNT.createMetric("maximumSize") }));
        WebSphereJmxValues.METRICS.add(new BaseJmxValue("WebSphere:j2eeType=JTAResource,type=TransactionService,name=*,process=*,platform=*,node=*,*", "JmxBuiltIn/JTA/{type}/", new JmxMetric[] { JtaJmxMetricGenerator.COMMIT.createMetric("stats.CommittedCount"), JtaJmxMetricGenerator.ROLLBACK.createMetric("stats.RolledbackCount"), JtaJmxMetricGenerator.TIMEOUT.createMetric("stats.GlobalTimeoutCount") }));
        WebSphereJmxValues.METRICS.add(new BaseJmxValue("WebSphere:type=SessionManager,name=*,process=*,platform=*,node=*,*", "JmxBuiltIn/Session/{name}/", new JmxMetric[] { ServerJmxMetricGenerator.SESSION_ACTIVE_COUNT.createMetric("stats.LiveCount") }));
    }
}
