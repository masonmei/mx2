// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class Glassfish2JmxValues extends JmxFrameworkValues
{
    private static final int METRIC_COUNT = 3;
    private static final List<BaseJmxValue> METRICS;
    private static String PREFIX;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return Glassfish2JmxValues.METRICS;
    }
    
    public String getPrefix() {
        return Glassfish2JmxValues.PREFIX;
    }
    
    static {
        METRICS = new ArrayList<BaseJmxValue>(3);
        Glassfish2JmxValues.PREFIX = "com.sun";
        Glassfish2JmxValues.METRICS.add(new BaseJmxValue("com.sun.appserv:name=*,type=thread-pool,category=monitor,server=*", "JmxBuiltIn/ThreadPool/{name}/", new JmxMetric[] { ServerJmxMetricGenerator.ACTIVE_THREAD_POOL_COUNT.createMetric("numberofbusythreads-count"), ServerJmxMetricGenerator.IDLE_THREAD_POOL_COUNT.createMetric("numberofavailablethreads-count") }));
        Glassfish2JmxValues.METRICS.add(new BaseJmxValue("com.sun.appserv:type=Manager,path=*,host=server", "JmxBuiltIn/Session/{path}/", new JmxMetric[] { ServerJmxMetricGenerator.SESSION_ACTIVE_COUNT.createMetric("activeSessions"), ServerJmxMetricGenerator.SESSION_EXPIRED_COUNT.createMetric("expiredSessions"), ServerJmxMetricGenerator.SESSION_REJECTED_COUNT.createMetric("rejectedSessions"), ServerJmxMetricGenerator.SESSION_AVG_ALIVE_TIME.createMetric("sessionAverageAliveTimeSeconds") }));
        Glassfish2JmxValues.METRICS.add(new BaseJmxValue("com.sun.appserv:type=transaction-service,category=monitor,server=*", "JmxBuiltIn/Transactions/", new JmxMetric[] { ServerJmxMetricGenerator.TRANS_ACTIVE_COUNT.createMetric("activecount-count"), ServerJmxMetricGenerator.TRANS_COMMITED_COUNT.createMetric("committedcount-count"), ServerJmxMetricGenerator.TRANS_ROLLED_BACK_COUNT.createMetric("rolledbackcount-count") }));
    }
}
