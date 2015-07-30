// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Collection;
import com.newrelic.agent.MetricData;
import com.newrelic.agent.metric.MetricIdRegistry;
import com.newrelic.agent.normalization.Normalizer;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.metric.MetricName;
import java.util.HashMap;
import java.util.Map;

public class StatsEngineImpl implements StatsEngine
{
    private static final float HASH_SET_LOAD_FACTOR = 0.75f;
    public static final int DEFAULT_CAPACITY = 140;
    public static final int DEFAULT_SCOPED_CAPACITY = 32;
    public static final int DOUBLE = 2;
    private final SimpleStatsEngine unscopedStats;
    private final Map<String, SimpleStatsEngine> scopedStats;
    
    public StatsEngineImpl() {
        this(140);
    }
    
    public StatsEngineImpl(final int capacity) {
        this.unscopedStats = new SimpleStatsEngine(capacity);
        this.scopedStats = new HashMap<String, SimpleStatsEngine>(capacity);
    }
    
    public Stats getStats(final String name) {
        return this.getStats(MetricName.create(name));
    }
    
    public Stats getStats(final MetricName metricName) {
        if (metricName == null) {
            throw new RuntimeException("Cannot get a stat for a null metric");
        }
        return this.getStatsEngine(metricName).getStats(metricName.getName());
    }
    
    public void recordEmptyStats(final String name) {
        this.recordEmptyStats(MetricName.create(name));
    }
    
    public void recordEmptyStats(final MetricName metricName) {
        if (metricName == null) {
            throw new RuntimeException("Cannot create stats for a null metric");
        }
        this.getStatsEngine(metricName).recordEmptyStats(metricName.getName());
    }
    
    private SimpleStatsEngine getStatsEngine(final MetricName metricName) {
        if (metricName.isScoped()) {
            SimpleStatsEngine statsEngine = this.scopedStats.get(metricName.getScope());
            if (statsEngine == null) {
                statsEngine = new SimpleStatsEngine(32);
                this.scopedStats.put(metricName.getScope(), statsEngine);
            }
            return statsEngine;
        }
        return this.unscopedStats;
    }
    
    public ResponseTimeStats getResponseTimeStats(final String name) {
        return this.getResponseTimeStats(MetricName.create(name));
    }
    
    public ResponseTimeStats getResponseTimeStats(final MetricName metricName) {
        if (metricName == null) {
            throw new RuntimeException("Cannot get a stat for a null metric");
        }
        return this.getStatsEngine(metricName).getResponseTimeStats(metricName.getName());
    }
    
    public ApdexStats getApdexStats(final MetricName metricName) {
        if (metricName == null) {
            throw new RuntimeException("Cannot get a stat for a null metric");
        }
        return this.getStatsEngine(metricName).getApdexStats(metricName.getName());
    }
    
    public List<MetricName> getMetricNames() {
        final List<MetricName> result = new ArrayList<MetricName>(this.getSize());
        for (final String name : this.unscopedStats.getStatsMap().keySet()) {
            result.add(MetricName.create(name));
        }
        for (final Map.Entry<String, SimpleStatsEngine> entry : this.scopedStats.entrySet()) {
            for (final String name2 : entry.getValue().getStatsMap().keySet()) {
                result.add(MetricName.create(name2, entry.getKey()));
            }
        }
        return result;
    }
    
    public void clear() {
        this.unscopedStats.clear();
        this.scopedStats.clear();
    }
    
    public int getSize() {
        int size = this.unscopedStats.getStatsMap().size();
        for (final SimpleStatsEngine engine : this.scopedStats.values()) {
            size += engine.getStatsMap().size();
        }
        return size;
    }
    
    public void mergeStats(final StatsEngine statsEngine) {
        if (statsEngine instanceof StatsEngineImpl) {
            this.mergeStats((StatsEngineImpl)statsEngine);
        }
    }
    
    private void mergeStats(final StatsEngineImpl other) {
        this.unscopedStats.mergeStats(other.unscopedStats);
        for (final Map.Entry<String, SimpleStatsEngine> entry : other.scopedStats.entrySet()) {
            SimpleStatsEngine scopedStatsEngine = this.scopedStats.get(entry.getKey());
            if (scopedStatsEngine == null) {
                scopedStatsEngine = new SimpleStatsEngine(entry.getValue().getSize());
                this.scopedStats.put(entry.getKey(), scopedStatsEngine);
            }
            scopedStatsEngine.mergeStats(entry.getValue());
        }
    }
    
    public void mergeStatsResolvingScope(final TransactionStats txStats, final String resolvedScope) {
        this.unscopedStats.mergeStats(txStats.getUnscopedStats());
        if (resolvedScope == null) {
            return;
        }
        SimpleStatsEngine scopedStatsEngine = this.scopedStats.get(resolvedScope);
        if (scopedStatsEngine == null) {
            scopedStatsEngine = new SimpleStatsEngine(txStats.getScopedStats().getSize());
            this.scopedStats.put(resolvedScope, scopedStatsEngine);
        }
        scopedStatsEngine.mergeStats(txStats.getScopedStats());
    }
    
    public List<MetricData> getMetricData(final Normalizer metricNormalizer, final MetricIdRegistry metricIdRegistry) {
        final List<MetricData> result = new ArrayList<MetricData>(this.unscopedStats.getStatsMap().size() + this.scopedStats.size() * 32 * 2);
        for (final Map.Entry<String, SimpleStatsEngine> entry : this.scopedStats.entrySet()) {
            result.addAll(entry.getValue().getMetricData(metricNormalizer, metricIdRegistry, entry.getKey()));
        }
        result.addAll(this.createUnscopedCopies(metricNormalizer, metricIdRegistry, result));
        result.addAll(this.unscopedStats.getMetricData(metricNormalizer, metricIdRegistry, ""));
        return aggregate(metricIdRegistry, result);
    }
    
    private List<MetricData> createUnscopedCopies(final Normalizer metricNormalizer, final MetricIdRegistry metricIdRegistry, final List<MetricData> scopedMetrics) {
        final int size = (int)(scopedMetrics.size() / 0.75) + 2;
        final Map<String, MetricData> allUnscopedMetrics = new HashMap<String, MetricData>(size);
        final List<MetricData> results = new ArrayList<MetricData>(scopedMetrics.size());
        for (final MetricData scoped : scopedMetrics) {
            final String theMetricName = scoped.getMetricName().getName();
            final MetricData unscopedMetric = this.getUnscopedCloneOfData(metricNormalizer, metricIdRegistry, theMetricName, scoped.getStats());
            if (unscopedMetric != null) {
                final MetricData mapUnscoped = allUnscopedMetrics.get(theMetricName);
                if (mapUnscoped == null) {
                    allUnscopedMetrics.put(theMetricName, unscopedMetric);
                    results.add(unscopedMetric);
                }
                else {
                    mapUnscoped.getStats().merge(unscopedMetric.getStats());
                }
            }
        }
        return results;
    }
    
    private MetricData getUnscopedCloneOfData(final Normalizer metricNormalizer, final MetricIdRegistry metricIdRegistry, final String metricName, final StatsBase stats) {
        if (stats != null) {
            final MetricName metricNameUnscoped = MetricName.create(metricName);
            try {
                final MetricData metricDataUnscoped = SimpleStatsEngine.createMetricData(metricNameUnscoped, (StatsBase)stats.clone(), metricNormalizer, metricIdRegistry);
                return metricDataUnscoped;
            }
            catch (CloneNotSupportedException e) {
                Agent.LOG.log(Level.INFO, "Unscoped metric not created because stats base could not be cloned for " + metricNameUnscoped.getName());
                return null;
            }
        }
        return null;
    }
    
    static List<MetricData> aggregate(final MetricIdRegistry metricIdRegistry, final List<MetricData> result) {
        if (metricIdRegistry.getSize() == 0) {
            return result;
        }
        final int hashMapSize = (int)(result.size() / 0.75f) + 1;
        final HashMap<Object, MetricData> data = new HashMap<Object, MetricData>(hashMapSize);
        for (final MetricData md : result) {
            final MetricData existing = data.get(md.getKey());
            if (existing == null) {
                data.put(md.getKey(), md);
            }
            else {
                existing.getStats().merge(md.getStats());
            }
        }
        if (data.size() == result.size()) {
            return result;
        }
        return new ArrayList<MetricData>(data.values());
    }
}
