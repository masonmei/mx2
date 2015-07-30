// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.JmxType;
import java.util.ArrayList;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxInit;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

@JmxInit
public class JavaLangJmxMetrics extends JmxFrameworkValues
{
    private static String PREFIX;
    private static final int METRIC_COUNT = 2;
    private static final List<BaseJmxValue> METRICS;
    private static final JmxMetric CURRENT_THREAD_COUNT;
    private static final JmxMetric TOTAL_THREAD_COUNT;
    private static final JmxMetric LOADED_CLASSES;
    private static final JmxMetric UNLOADED_CLASSES;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return JavaLangJmxMetrics.METRICS;
    }
    
    public String getPrefix() {
        return JavaLangJmxMetrics.PREFIX;
    }
    
    static {
        JavaLangJmxMetrics.PREFIX = "java.lang";
        METRICS = new ArrayList<BaseJmxValue>(2);
        CURRENT_THREAD_COUNT = JmxMetric.create("ThreadCount", "Thread Count", JmxType.SIMPLE);
        TOTAL_THREAD_COUNT = JmxMetric.create("TotalStartedThreadCount", "TotalStartedCount", JmxType.SIMPLE);
        LOADED_CLASSES = JmxMetric.create("LoadedClassCount", "Loaded", JmxType.SIMPLE);
        UNLOADED_CLASSES = JmxMetric.create("UnloadedClassCount", "Unloaded", JmxType.SIMPLE);
        JavaLangJmxMetrics.METRICS.add(new BaseJmxValue("java.lang:type=Threading", "JmxBuiltIn/Threads/", new JmxMetric[] { JavaLangJmxMetrics.CURRENT_THREAD_COUNT, JavaLangJmxMetrics.TOTAL_THREAD_COUNT }));
        JavaLangJmxMetrics.METRICS.add(new BaseJmxValue("java.lang:type=ClassLoading", "JmxBuiltIn/Classes/", new JmxMetric[] { JavaLangJmxMetrics.LOADED_CLASSES, JavaLangJmxMetrics.UNLOADED_CLASSES }));
    }
}
