// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.BlockingQueue;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import com.newrelic.agent.service.StatsServiceMetricAggregator;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.api.agent.MetricAggregator;
import com.newrelic.agent.service.AbstractService;

public class StatsServiceImpl extends AbstractService implements StatsService
{
    private final MetricAggregator metricAggregator;
    private final ConcurrentMap<String, StatsEngineQueue> statsEngineQueues;
    private volatile StatsEngineQueue defaultStatsEngineQueue;
    private final String defaultAppName;
    
    public StatsServiceImpl() {
        super(StatsService.class.getSimpleName());
        this.metricAggregator = (MetricAggregator)new StatsServiceMetricAggregator(this);
        this.statsEngineQueues = new ConcurrentHashMap<String, StatsEngineQueue>();
        this.defaultAppName = ServiceFactory.getConfigService().getDefaultAgentConfig().getApplicationName();
        this.defaultStatsEngineQueue = this.createStatsEngineQueue();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() {
    }
    
    protected void doStop() {
    }
    
    public void doStatsWork(final StatsWork work) {
        final String appName = work.getAppName();
        for (boolean done = false; !done; done = this.getOrCreateStatsEngineQueue(appName).doStatsWork(work)) {}
    }
    
    public StatsEngine getStatsEngineForHarvest(final String appName) {
        final StatsEngineQueue oldStatsEngineQueue = this.replaceStatsEngineQueue(appName);
        oldStatsEngineQueue.close();
        return oldStatsEngineQueue.getStatsEngineForHarvest();
    }
    
    public MetricAggregator getMetricAggregator() {
        return this.metricAggregator;
    }
    
    private StatsEngineQueue replaceStatsEngineQueue(final String appName) {
        final StatsEngineQueue oldStatsEngineQueue = this.getOrCreateStatsEngineQueue(appName);
        final StatsEngineQueue newStatsEngineQueue = this.createStatsEngineQueue();
        if (oldStatsEngineQueue == this.defaultStatsEngineQueue) {
            this.defaultStatsEngineQueue = newStatsEngineQueue;
        }
        else {
            this.statsEngineQueues.put(appName, newStatsEngineQueue);
        }
        return oldStatsEngineQueue;
    }
    
    private StatsEngineQueue getOrCreateStatsEngineQueue(final String appName) {
        StatsEngineQueue statsEngineQueue = this.getStatsEngineQueue(appName);
        if (statsEngineQueue != null) {
            return statsEngineQueue;
        }
        statsEngineQueue = this.createStatsEngineQueue();
        final StatsEngineQueue oldStatsEngineQueue = this.statsEngineQueues.putIfAbsent(appName, statsEngineQueue);
        return (oldStatsEngineQueue == null) ? statsEngineQueue : oldStatsEngineQueue;
    }
    
    private StatsEngineQueue getStatsEngineQueue(final String appName) {
        if (appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultStatsEngineQueue;
        }
        return this.statsEngineQueues.get(appName);
    }
    
    private StatsEngineQueue createStatsEngineQueue() {
        return new StatsEngineQueue();
    }
    
    private static class StatsEngineQueue
    {
        private volatile boolean isClosed;
        private final BlockingQueue<StatsEngine> statsEngineQueue;
        private final Lock readLock;
        private final Lock writeLock;
        private final AtomicInteger statsEngineCount;
        
        private StatsEngineQueue() {
            this.isClosed = false;
            this.statsEngineQueue = new LinkedBlockingQueue<StatsEngine>();
            this.statsEngineCount = new AtomicInteger();
            final ReadWriteLock lock = new ReentrantReadWriteLock();
            this.readLock = lock.readLock();
            this.writeLock = lock.writeLock();
        }
        
        public boolean doStatsWork(final StatsWork work) {
            if (this.readLock.tryLock()) {
                try {
                    if (this.isClosed()) {
                        final boolean b = false;
                        this.readLock.unlock();
                        return b;
                    }
                    this.doStatsWorkUnderLock(work);
                    final boolean b2 = true;
                    this.readLock.unlock();
                    return b2;
                }
                finally {
                    this.readLock.unlock();
                }
            }
            return false;
        }
        
        private void doStatsWorkUnderLock(final StatsWork work) {
            StatsEngine statsEngine = null;
            try {
                statsEngine = this.statsEngineQueue.poll();
                if (statsEngine == null) {
                    statsEngine = this.createStatsEngine();
                    this.statsEngineCount.incrementAndGet();
                }
                work.doWork(statsEngine);
                if (statsEngine != null) {
                    try {
                        if (!this.statsEngineQueue.offer(statsEngine)) {
                            Agent.LOG.warning("Failed to return stats engine to queue");
                        }
                    }
                    catch (Exception e) {
                        final String msg = MessageFormat.format("Exception returning stats engine to queue: {0}", e);
                        Agent.LOG.warning(msg);
                    }
                }
            }
            catch (Exception e2) {
                final String msg2 = MessageFormat.format("Exception doing stats work: {0}", e2);
                Agent.LOG.warning(msg2);
                if (statsEngine != null) {
                    try {
                        if (!this.statsEngineQueue.offer(statsEngine)) {
                            Agent.LOG.warning("Failed to return stats engine to queue");
                        }
                    }
                    catch (Exception e) {
                        final String msg = MessageFormat.format("Exception returning stats engine to queue: {0}", e);
                        Agent.LOG.warning(msg);
                    }
                }
            }
            finally {
                if (statsEngine != null) {
                    try {
                        if (!this.statsEngineQueue.offer(statsEngine)) {
                            Agent.LOG.warning("Failed to return stats engine to queue");
                        }
                    }
                    catch (Exception e) {
                        final String msg = MessageFormat.format("Exception returning stats engine to queue: {0}", e);
                        Agent.LOG.warning(msg);
                    }
                }
            }
        }
        
        public StatsEngine getStatsEngineForHarvest() {
            this.writeLock.lock();
            try {
                final StatsEngine statsEngineForHarvestUnderLock = this.getStatsEngineForHarvestUnderLock();
                this.writeLock.unlock();
                return statsEngineForHarvestUnderLock;
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        private StatsEngine getStatsEngineForHarvestUnderLock() {
            final List<StatsEngine> statsEngines = new ArrayList<StatsEngine>();
            try {
                this.statsEngineQueue.drainTo(statsEngines);
            }
            catch (Exception e) {
                final String msg = MessageFormat.format("Exception draining stats engine queue: {0}", e);
                Agent.LOG.warning(msg);
            }
            if (statsEngines.size() != this.statsEngineCount.get()) {
                final String msg2 = MessageFormat.format("Error draining stats engine queue. Expected: {0} actual: {1}", this.statsEngineCount.get(), statsEngines.size());
                Agent.LOG.warning(msg2);
            }
            final StatsEngine harvestStatsEngine = this.createStatsEngine();
            for (final StatsEngine statsEngine : statsEngines) {
                harvestStatsEngine.mergeStats(statsEngine);
            }
            return harvestStatsEngine;
        }
        
        private StatsEngine createStatsEngine() {
            return new StatsEngineImpl();
        }
        
        public void close() {
            this.isClosed = true;
        }
        
        private boolean isClosed() {
            return this.isClosed;
        }
    }
}
