// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import com.newrelic.agent.jmx.JmxType;
import com.newrelic.agent.jmx.metrics.JmxAction;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class Jboss56JmxValues extends JmxFrameworkValues
{
    private static String PREFIX;
    private static final int METRIC_COUNT = 2;
    private static final List<BaseJmxValue> METRICS;
    private static final JmxMetric ACTIVE_SESSIONS;
    private static final JmxMetric SESSION_ALIVE_TIME;
    private static final JmxMetric EXPIRED_SESSIONS;
    private static final JmxMetric REJECTED_SESSIONS;
    private static final JmxMetric CURRENT_MAX_COUNT;
    private static final JmxMetric CURRENT_ACTIVE_COUNT;
    private static final JmxMetric CURRENT_IDLE_COUNT;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return Jboss56JmxValues.METRICS;
    }
    
    public String getPrefix() {
        return Jboss56JmxValues.PREFIX;
    }
    
    static {
        Jboss56JmxValues.PREFIX = "jboss.web";
        METRICS = new ArrayList<BaseJmxValue>(2);
        ACTIVE_SESSIONS = JmxMetric.create(new String[] { "activeSessions", "ActiveSessions" }, "Active", JmxAction.USE_FIRST_RECORDED_ATT, JmxType.SIMPLE);
        SESSION_ALIVE_TIME = JmxMetric.create(new String[] { "sessionAverageAliveTime", "SessionAverageAliveTime" }, "AverageAliveTime", JmxAction.USE_FIRST_RECORDED_ATT, JmxType.SIMPLE);
        EXPIRED_SESSIONS = JmxMetric.create(new String[] { "expiredSessions", "ExpiredSessions" }, "Expired", JmxAction.USE_FIRST_RECORDED_ATT, JmxType.MONOTONICALLY_INCREASING);
        REJECTED_SESSIONS = JmxMetric.create(new String[] { "rejectedSessions", "RejectedSessions" }, "Rejected", JmxAction.USE_FIRST_RECORDED_ATT, JmxType.MONOTONICALLY_INCREASING);
        CURRENT_MAX_COUNT = ServerJmxMetricGenerator.MAX_THREAD_POOL_COUNT.createMetric("maxThreads");
        CURRENT_ACTIVE_COUNT = ServerJmxMetricGenerator.ACTIVE_THREAD_POOL_COUNT.createMetric("currentThreadsBusy");
        CURRENT_IDLE_COUNT = JmxMetric.create(new String[] { "currentThreadCount", "currentThreadsBusy" }, "Idle", JmxAction.SUBTRACT_ALL_FROM_FIRST, JmxType.SIMPLE);
        Jboss56JmxValues.METRICS.add(new BaseJmxValue("jboss.web:type=ThreadPool,name=*", "JmxBuiltIn/ThreadPool/{name}/", new JmxMetric[] { Jboss56JmxValues.CURRENT_ACTIVE_COUNT, Jboss56JmxValues.CURRENT_IDLE_COUNT, Jboss56JmxValues.CURRENT_MAX_COUNT }));
        Jboss56JmxValues.METRICS.add(new BaseJmxValue("jboss.web:type=Manager,path=*,host=*", "JmxBuiltIn/Session/{path}/", new JmxMetric[] { Jboss56JmxValues.ACTIVE_SESSIONS, Jboss56JmxValues.EXPIRED_SESSIONS, Jboss56JmxValues.REJECTED_SESSIONS, Jboss56JmxValues.SESSION_ALIVE_TIME }));
    }
}
