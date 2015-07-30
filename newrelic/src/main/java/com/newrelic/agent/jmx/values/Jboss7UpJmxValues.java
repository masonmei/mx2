// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.JmxType;
import com.newrelic.agent.jmx.metrics.JmxAction;
import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class Jboss7UpJmxValues extends JmxFrameworkValues
{
    private static final int METRIC_COUNT = 1;
    private static final List<BaseJmxValue> METRICS;
    private static String PREFIX;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return Jboss7UpJmxValues.METRICS;
    }
    
    public String getPrefix() {
        return Jboss7UpJmxValues.PREFIX;
    }
    
    static {
        METRICS = new ArrayList<BaseJmxValue>(1);
        Jboss7UpJmxValues.PREFIX = "jboss.as";
        Jboss7UpJmxValues.METRICS.add(new BaseJmxValue("jboss.as:subsystem=transactions", "JmxBuiltIn/Transactions/", new JmxMetric[] { ServerJmxMetricGenerator.TRANS_ROLLED_BACK_COUNT.createMetric("numberOfAbortedTransactions"), ServerJmxMetricGenerator.TRANS_COMMITED_COUNT.createMetric("numberOfCommittedTransactions"), ServerJmxMetricGenerator.TRANS_ACTIVE_COUNT.createMetric("numberOfInflightTransactions"), ServerJmxMetricGenerator.TRANS_NESTED_COUNT.createMetric("numberOfNestedTransactions"), JmxMetric.create(new String[] { "numberOfTransactions", "numberOfNestedTransactions" }, "Created/Top Level", JmxAction.SUBTRACT_ALL_FROM_FIRST, JmxType.MONOTONICALLY_INCREASING) }));
    }
}
