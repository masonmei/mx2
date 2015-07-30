// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Iterator;
import java.util.List;
import java.lang.management.MemoryPoolMXBean;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.stats.StatsEngine;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class MemorySampler implements MetricSampler
{
    public static final float BYTES_PER_MB = 1048576.0f;
    private final MemoryMXBean memoryMXBean;
    
    public MemorySampler() {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }
    
    void start() {
    }
    
    public void sample(final StatsEngine statsEngine) {
        this.sampleMemory(statsEngine);
        this.sampleMemoryPools(statsEngine);
    }
    
    private void sampleMemory(final StatsEngine statsEngine) {
        try {
            final HeapAndNonHeapUsage heapUsage = new HeapAndNonHeapUsage(this.memoryMXBean);
            heapUsage.recordStats(statsEngine);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("An error occurred gathering memory metrics: {0}", e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.WARNING, msg, e);
            }
            else {
                Agent.LOG.warning(msg);
            }
        }
    }
    
    private void sampleMemoryPools(final StatsEngine statsEngine) {
        try {
            final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            for (final MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                final PoolUsage poolUsage = new PoolUsage(memoryPoolMXBean);
                poolUsage.recordStats(statsEngine);
            }
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("An error occurred gathering memory pool metrics: {0}", e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.WARNING, msg, e);
            }
            else {
                Agent.LOG.warning(msg);
            }
        }
    }
    
    private static final class HeapAndNonHeapUsage
    {
        private final long heapUsed;
        private final long heapCommitted;
        private final long heapMax;
        private final long nonHeapUsed;
        private final long nonHeapCommitted;
        private final long nonHeapMax;
        
        private HeapAndNonHeapUsage(final MemoryMXBean memoryMXBean) {
            final MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
            this.nonHeapCommitted = nonHeapMemoryUsage.getCommitted();
            this.nonHeapUsed = nonHeapMemoryUsage.getUsed();
            this.nonHeapMax = ((nonHeapMemoryUsage.getMax() == -1L) ? 0L : nonHeapMemoryUsage.getMax());
            final MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            this.heapUsed = heapMemoryUsage.getUsed();
            this.heapCommitted = heapMemoryUsage.getCommitted();
            this.heapMax = ((heapMemoryUsage.getMax() == -1L) ? 0L : heapMemoryUsage.getMax());
        }
        
        public void recordStats(final StatsEngine statsEngine) {
            statsEngine.getStats("Memory/Physical").recordDataPoint(this.getCommitted());
            statsEngine.getStats("Memory/Used").recordDataPoint(this.getUsed());
            statsEngine.getStats("Memory/Heap/Used").recordDataPoint(this.getHeapUsed());
            statsEngine.getStats("Memory/Heap/Committed").recordDataPoint(this.getHeapCommitted());
            statsEngine.getStats("Memory/Heap/Max").recordDataPoint(this.getHeapMax());
            statsEngine.getStats("Memory/Heap/Utilization").recordDataPoint(this.getHeapUtilization());
            statsEngine.getStats("Memory/NonHeap/Used").recordDataPoint(this.getNonHeapUsed());
            statsEngine.getStats("Memory/NonHeap/Committed").recordDataPoint(this.getNonHeapCommitted());
            statsEngine.getStats("Memory/NonHeap/Max").recordDataPoint(this.getNonHeapMax());
        }
        
        private float getCommitted() {
            return (this.nonHeapCommitted + this.heapCommitted) / 1048576.0f;
        }
        
        private float getUsed() {
            return (this.nonHeapUsed + this.heapUsed) / 1048576.0f;
        }
        
        private float getHeapUsed() {
            return this.heapUsed / 1048576.0f;
        }
        
        private float getHeapUtilization() {
            return (this.heapMax == 0L) ? 0.0f : (this.heapUsed / this.heapMax);
        }
        
        private float getHeapCommitted() {
            return this.heapCommitted / 1048576.0f;
        }
        
        private float getHeapMax() {
            return this.heapMax / 1048576.0f;
        }
        
        private float getNonHeapUsed() {
            return this.nonHeapUsed / 1048576.0f;
        }
        
        private float getNonHeapCommitted() {
            return this.nonHeapCommitted / 1048576.0f;
        }
        
        private float getNonHeapMax() {
            return this.nonHeapMax / 1048576.0f;
        }
    }
    
    private static final class PoolUsage
    {
        private final String name;
        private final String type;
        private final long used;
        private final long committed;
        private final long max;
        
        private PoolUsage(final MemoryPoolMXBean memoryPoolMXBean) {
            this.name = memoryPoolMXBean.getName();
            this.type = ((memoryPoolMXBean.getType() == MemoryType.HEAP) ? "Heap" : "Non-Heap");
            final MemoryUsage memoryUsage = memoryPoolMXBean.getUsage();
            this.used = memoryUsage.getUsed();
            this.committed = memoryUsage.getCommitted();
            this.max = ((memoryUsage.getMax() == -1L) ? 0L : memoryUsage.getMax());
        }
        
        public void recordStats(final StatsEngine statsEngine) {
            String metricName = MessageFormat.format("MemoryPool/{0}/{1}/Used", this.type, this.name);
            statsEngine.getStats(metricName).recordDataPoint(this.getUsed());
            metricName = MessageFormat.format("MemoryPool/{0}/{1}/Committed", this.type, this.name);
            statsEngine.getStats(metricName).recordDataPoint(this.getCommitted());
            metricName = MessageFormat.format("MemoryPool/{0}/{1}/Max", this.type, this.name);
            statsEngine.getStats(metricName).recordDataPoint(this.getMax());
        }
        
        private float getUsed() {
            return this.used / 1048576.0f;
        }
        
        private float getCommitted() {
            return this.committed / 1048576.0f;
        }
        
        private float getMax() {
            return this.max / 1048576.0f;
        }
    }
}
