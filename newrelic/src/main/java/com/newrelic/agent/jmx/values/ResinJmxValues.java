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

public class ResinJmxValues extends JmxFrameworkValues
{
    private static String PREFIX;
    private static final int METRIC_COUNT = 3;
    private static final List<BaseJmxValue> METRICS;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return ResinJmxValues.METRICS;
    }
    
    public String getPrefix() {
        return ResinJmxValues.PREFIX;
    }
    
    static {
        ResinJmxValues.PREFIX = "resin";
        (METRICS = new ArrayList<BaseJmxValue>(3)).add(new BaseJmxValue("resin:type=SessionManager,*", "JmxBuiltIn/Session/{WebApp}/", new JmxMetric[] { ServerJmxMetricGenerator.SESSION_ACTIVE_COUNT.createMetric("SessionActiveCount"), ServerJmxMetricGenerator.SESSION_EXPIRED_COUNT.createMetric("SessionTimeoutCountTotal") }));
        ResinJmxValues.METRICS.add(new BaseJmxValue("resin:type=ThreadPool", "JmxBuiltIn/ThreadPool/Resin/", new JmxMetric[] { ServerJmxMetricGenerator.ACTIVE_THREAD_POOL_COUNT.createMetric("ThreadActiveCount"), ServerJmxMetricGenerator.IDLE_THREAD_POOL_COUNT.createMetric("ThreadIdleCount"), ServerJmxMetricGenerator.MAX_THREAD_POOL_COUNT.createMetric("ThreadMax") }));
        ResinJmxValues.METRICS.add(new BaseJmxValue("resin:type=TransactionManager", "JmxBuiltIn/Transactions/", new JmxMetric[] { ServerJmxMetricGenerator.TRANS_ROLLED_BACK_COUNT.createMetric("RollbackCountTotal"), ServerJmxMetricGenerator.TRANS_COMMITED_COUNT.createMetric("CommitCountTotal"), ServerJmxMetricGenerator.TRANS_ACTIVE_COUNT.createMetric("TransactionCount") }));
    }
}
