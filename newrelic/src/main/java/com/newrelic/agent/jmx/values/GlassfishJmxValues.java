// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import com.newrelic.agent.jmx.JmxType;
import com.newrelic.agent.jmx.metrics.JmxAction;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import com.newrelic.agent.jmx.metrics.BaseJmxInvokeValue;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class GlassfishJmxValues extends JmxFrameworkValues
{
    private static final int METRIC_COUNT = 3;
    private static final int INVOKE_COUNT = 1;
    private static final List<BaseJmxValue> METRICS;
    private static final List<BaseJmxInvokeValue> INVOKERS;
    private static String PREFIX;
    private static final JmxMetric CURRENT_IDLE_COUNT;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return GlassfishJmxValues.METRICS;
    }
    
    public String getPrefix() {
        return GlassfishJmxValues.PREFIX;
    }
    
    public List<BaseJmxInvokeValue> getJmxInvokers() {
        return GlassfishJmxValues.INVOKERS;
    }
    
    static {
        METRICS = new ArrayList<BaseJmxValue>(3);
        INVOKERS = new ArrayList<BaseJmxInvokeValue>(1);
        GlassfishJmxValues.PREFIX = "amx";
        CURRENT_IDLE_COUNT = JmxMetric.create(new String[] { "currentthreadcount.count", "currentthreadsbusy.count" }, "Idle", JmxAction.SUBTRACT_ALL_FROM_FIRST, JmxType.SIMPLE);
        GlassfishJmxValues.METRICS.add(new BaseJmxValue("amx:type=thread-pool-mon,pp=*,name=*", "JmxBuiltIn/ThreadPool/{name}/", new JmxMetric[] { ServerJmxMetricGenerator.ACTIVE_THREAD_POOL_COUNT.createMetric("currentthreadsbusy.count"), ServerJmxMetricGenerator.MAX_THREAD_POOL_COUNT.createMetric("maxthreads.count"), GlassfishJmxValues.CURRENT_IDLE_COUNT }));
        GlassfishJmxValues.METRICS.add(new BaseJmxValue("amx:type=session-mon,pp=*,name=*", "JmxBuiltIn/Session/{name}/", new JmxMetric[] { ServerJmxMetricGenerator.SESSION_ACTIVE_COUNT.createMetric("activesessionscurrent.current"), ServerJmxMetricGenerator.SESSION_EXPIRED_COUNT.createMetric("expiredsessionstotal.count"), ServerJmxMetricGenerator.SESSION_REJECTED_COUNT.createMetric("rejectedsessionstotal.count") }));
        GlassfishJmxValues.METRICS.add(new BaseJmxValue("amx:type=transaction-service-mon,pp=*,name=*", "JmxBuiltIn/Transactions/", new JmxMetric[] { ServerJmxMetricGenerator.TRANS_ACTIVE_COUNT.createMetric("activecount.count"), ServerJmxMetricGenerator.TRANS_COMMITED_COUNT.createMetric("committedcount.count"), ServerJmxMetricGenerator.TRANS_ROLLED_BACK_COUNT.createMetric("rolledbackcount.count") }));
        GlassfishJmxValues.INVOKERS.add(new BaseJmxInvokeValue("amx-support:type=boot-amx", "bootAMX", new Object[0], new String[0]));
    }
}
