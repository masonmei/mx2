// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.stats.ResponseTimeStats;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.service.ServiceFactory;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.service.AbstractService;

public class GCService extends AbstractService implements HarvestListener
{
    private final Map<String, GarbageCollector> garbageCollectors;
    
    public GCService() {
        super(GCService.class.getSimpleName());
        this.garbageCollectors = new HashMap<String, GarbageCollector>();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() {
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() {
    }
    
    public synchronized void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        try {
            this.harvestGC(statsEngine);
        }
        catch (Exception e) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Error harvesting GC metrics for {0}: {1}", appName, e);
                Agent.LOG.finer(msg);
            }
        }
    }
    
    private void harvestGC(final StatsEngine statsEngine) {
        for (final GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            GarbageCollector garbageCollector = this.garbageCollectors.get(gcBean.getName());
            if (garbageCollector == null) {
                garbageCollector = new GarbageCollector(gcBean);
                this.garbageCollectors.put(gcBean.getName(), garbageCollector);
            }
            else {
                garbageCollector.recordGC(gcBean, statsEngine);
            }
        }
    }
    
    public void afterHarvest(final String appName) {
    }
    
    private class GarbageCollector
    {
        private long collectionCount;
        private long collectionTime;
        
        public GarbageCollector(final GarbageCollectorMXBean gcBean) {
            this.collectionCount = gcBean.getCollectionCount();
            this.collectionTime = gcBean.getCollectionTime();
        }
        
        private void recordGC(final GarbageCollectorMXBean gcBean, final StatsEngine statsEngine) {
            final long lastCollectionCount = this.collectionCount;
            final long lastCollectionTime = this.collectionTime;
            this.collectionCount = gcBean.getCollectionCount();
            this.collectionTime = gcBean.getCollectionTime();
            final long numberOfCollections = this.collectionCount - lastCollectionCount;
            final long time = this.collectionTime - lastCollectionTime;
            if (numberOfCollections > 0L) {
                final String rootMetricName = "GC/" + gcBean.getName();
                final ResponseTimeStats stats = statsEngine.getResponseTimeStats(rootMetricName);
                stats.recordResponseTime(time, TimeUnit.MILLISECONDS);
                stats.setCallCount((int)numberOfCollections);
            }
        }
    }
}
