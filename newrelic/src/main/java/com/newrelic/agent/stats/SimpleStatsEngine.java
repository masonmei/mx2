// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.Set;
import java.util.HashSet;
import com.newrelic.agent.metric.MetricName;
import com.newrelic.agent.service.ServiceFactory;
import java.util.ArrayList;
import com.newrelic.agent.MetricData;
import java.util.List;
import com.newrelic.agent.metric.MetricIdRegistry;
import com.newrelic.agent.normalization.Normalizer;
import java.util.Iterator;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SimpleStatsEngine
{
    private static final float SCOPED_METRIC_THRESHOLD = 0.02f;
    public static final int DEFAULT_CAPACITY = 32;
    private final Map<String, StatsBase> stats;
    
    public SimpleStatsEngine() {
        this(32);
    }
    
    public SimpleStatsEngine(final int capacity) {
        this.stats = new HashMap<String, StatsBase>(capacity);
    }
    
    public Map<String, StatsBase> getStatsMap() {
        return this.stats;
    }
    
    public Stats getStats(final String metricName) {
        if (metricName == null) {
            throw new RuntimeException("Cannot get a stat for a null metric");
        }
        StatsBase s = this.stats.get(metricName);
        if (s == null) {
            s = new StatsImpl();
            this.stats.put(metricName, s);
        }
        if (s instanceof Stats) {
            return (Stats)s;
        }
        final String msg = MessageFormat.format("The stats object for {0} is of type {1}", metricName, s.getClass().getName());
        throw new RuntimeException(msg);
    }
    
    public ResponseTimeStats getResponseTimeStats(final String metric) {
        if (metric == null) {
            throw new RuntimeException("Cannot get a stat for a null metric");
        }
        StatsBase s = this.stats.get(metric);
        if (s == null) {
            s = new ResponseTimeStatsImpl();
            this.stats.put(metric, s);
        }
        if (s instanceof ResponseTimeStats) {
            return (ResponseTimeStats)s;
        }
        final String msg = MessageFormat.format("The stats object for {0} is of type {1}", metric, s.getClass().getName());
        throw new RuntimeException(msg);
    }
    
    public void recordEmptyStats(final String metricName) {
        if (metricName == null) {
            throw new RuntimeException("Cannot record a stat for a null metric");
        }
        this.stats.put(metricName, AbstractStats.EMPTY_STATS);
    }
    
    public ApdexStats getApdexStats(final String metricName) {
        if (metricName == null) {
            throw new RuntimeException("Cannot get a stat for a null metric");
        }
        StatsBase s = this.stats.get(metricName);
        if (s == null) {
            s = new ApdexStatsImpl();
            this.stats.put(metricName, s);
        }
        if (s instanceof ApdexStats) {
            return (ApdexStats)s;
        }
        final String msg = MessageFormat.format("The stats object for {0} is of type {1}", metricName, s.getClass().getName());
        throw new RuntimeException(msg);
    }
    
    public void mergeStats(final SimpleStatsEngine other) {
        for (final Map.Entry<String, StatsBase> entry : other.stats.entrySet()) {
            final StatsBase ourStats = this.stats.get(entry.getKey());
            final StatsBase otherStats = entry.getValue();
            if (ourStats == null) {
                this.stats.put(entry.getKey(), otherStats);
            }
            else {
                ourStats.merge(otherStats);
            }
        }
    }
    
    public void clear() {
        this.stats.clear();
    }
    
    public int getSize() {
        return this.stats.size();
    }
    
    public List<MetricData> getMetricData(final Normalizer metricNormalizer, final MetricIdRegistry metricIdRegistry, final String scope) {
        final List<MetricData> result = new ArrayList<MetricData>(this.stats.size() + 1);
        final boolean isTrimStats = ServiceFactory.getConfigService().getDefaultAgentConfig().isTrimStats();
        if (isTrimStats && scope != "") {
            this.trimStats();
        }
        for (final Map.Entry<String, StatsBase> entry : this.stats.entrySet()) {
            final MetricName metricName = MetricName.create(entry.getKey(), scope);
            final MetricData metricData = createMetricData(metricName, entry.getValue(), metricNormalizer, metricIdRegistry);
            if (metricData != null) {
                result.add(metricData);
            }
        }
        return result;
    }
    
    protected static MetricData createMetricData(final MetricName metricName, final StatsBase statsBase, final Normalizer metricNormalizer, final MetricIdRegistry metricIdRegistry) {
        if (!statsBase.hasData()) {
            return null;
        }
        Integer metricId = metricIdRegistry.getMetricId(metricName);
        if (metricId != null) {
            return MetricData.create(metricName, metricId, statsBase);
        }
        final String normalized = metricNormalizer.normalize(metricName.getName());
        if (normalized == null) {
            return null;
        }
        if (normalized == metricName.getName()) {
            return MetricData.create(metricName, statsBase);
        }
        final MetricName normalizedMetricName = MetricName.create(normalized, metricName.getScope());
        metricId = metricIdRegistry.getMetricId(normalizedMetricName);
        if (metricId == null) {
            return MetricData.create(normalizedMetricName, statsBase);
        }
        return MetricData.create(normalizedMetricName, metricId, statsBase);
    }
    
    private void trimStats() {
        float totalTime = 0.0f;
        for (final StatsBase statsBase : this.stats.values()) {
            final ResponseTimeStats stats = (ResponseTimeStats)statsBase;
            totalTime += stats.getTotalExclusiveTime();
        }
        ResponseTimeStatsImpl other = null;
        final float threshold = totalTime * 0.02f;
        final Set<String> remove = new HashSet<String>();
        for (final Map.Entry<String, StatsBase> entry : this.stats.entrySet()) {
            final ResponseTimeStatsImpl statsObj = (ResponseTimeStatsImpl)entry.getValue();
            if (statsObj.getTotalExclusiveTime() < threshold && this.trimmableMetric(entry.getKey())) {
                if (other == null) {
                    other = statsObj;
                }
                else {
                    other.merge(statsObj);
                }
                remove.add(entry.getKey());
            }
        }
        if (other != null) {
            this.stats.put("Java/other", other);
            for (final String name : remove) {
                this.stats.remove(name);
            }
        }
    }
    
    private boolean trimmableMetric(final String key) {
        return !key.startsWith("Datastore") && !key.startsWith("External") && !key.startsWith("RequestDispatcher");
    }
    
    public String toString() {
        return "SimpleStatsEngine [stats=" + this.stats + "]";
    }
}
