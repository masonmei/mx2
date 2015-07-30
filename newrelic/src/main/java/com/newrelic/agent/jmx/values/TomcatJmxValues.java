// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.JmxType;
import com.newrelic.agent.jmx.metrics.JmxAction;
import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class TomcatJmxValues extends JmxFrameworkValues
{
    private static String PREFIX;
    private static final int METRIC_COUNT = 3;
    private static final JmxMetric ACTIVE_SESSIONS;
    private static final JmxMetric EXPIRED_SESSIONS;
    private static final JmxMetric REJECTED_SESSIONS;
    private static final JmxMetric SESSION_ALIVE_TIME;
    private static final JmxMetric CURRENT_MAX_COUNT;
    private static final JmxMetric CURRENT_ACTIVE_COUNT;
    private static final JmxMetric CURRENT_IDLE_COUNT;
    private final List<BaseJmxValue> metrics;
    
    public TomcatJmxValues() {
        this.metrics = new ArrayList<BaseJmxValue>(3);
        this.createMetrics("*");
    }
    
    public TomcatJmxValues(final String name) {
        this.metrics = new ArrayList<BaseJmxValue>(3);
        this.createMetrics(name);
    }
    
    private void createMetrics(final String name) {
        this.metrics.add(new BaseJmxValue(name + ":type=Manager,context=*,host=*,*", "JmxBuiltIn/Session/{context}/", new JmxMetric[] { TomcatJmxValues.ACTIVE_SESSIONS, TomcatJmxValues.EXPIRED_SESSIONS, TomcatJmxValues.REJECTED_SESSIONS, TomcatJmxValues.SESSION_ALIVE_TIME }));
        this.metrics.add(new BaseJmxValue(name + ":type=Manager,path=*,host=*", "JmxBuiltIn/Session/{path}/", new JmxMetric[] { TomcatJmxValues.ACTIVE_SESSIONS, TomcatJmxValues.EXPIRED_SESSIONS, TomcatJmxValues.REJECTED_SESSIONS, TomcatJmxValues.SESSION_ALIVE_TIME }));
        this.metrics.add(new BaseJmxValue(name + ":type=ThreadPool,name=*", "JmxBuiltIn/ThreadPool/{name}/", new JmxMetric[] { TomcatJmxValues.CURRENT_ACTIVE_COUNT, TomcatJmxValues.CURRENT_IDLE_COUNT, TomcatJmxValues.CURRENT_MAX_COUNT }));
    }
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return this.metrics;
    }
    
    public String getPrefix() {
        return TomcatJmxValues.PREFIX;
    }
    
    static {
        TomcatJmxValues.PREFIX = "Catalina";
        ACTIVE_SESSIONS = ServerJmxMetricGenerator.SESSION_ACTIVE_COUNT.createMetric("activeSessions");
        EXPIRED_SESSIONS = ServerJmxMetricGenerator.SESSION_EXPIRED_COUNT.createMetric("expiredSessions");
        REJECTED_SESSIONS = ServerJmxMetricGenerator.SESSION_REJECTED_COUNT.createMetric("rejectedSessions");
        SESSION_ALIVE_TIME = ServerJmxMetricGenerator.SESSION_AVG_ALIVE_TIME.createMetric("sessionAverageAliveTime");
        CURRENT_MAX_COUNT = ServerJmxMetricGenerator.MAX_THREAD_POOL_COUNT.createMetric("maxThreads");
        CURRENT_ACTIVE_COUNT = ServerJmxMetricGenerator.ACTIVE_THREAD_POOL_COUNT.createMetric("currentThreadsBusy");
        CURRENT_IDLE_COUNT = JmxMetric.create(new String[] { "currentThreadCount", "currentThreadsBusy" }, "Idle", JmxAction.SUBTRACT_ALL_FROM_FIRST, JmxType.SIMPLE);
    }
}
