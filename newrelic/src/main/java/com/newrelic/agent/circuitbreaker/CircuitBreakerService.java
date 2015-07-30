// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.circuitbreaker;

import com.newrelic.agent.RPMService;
import com.newrelic.agent.config.AgentConfig;
import java.util.concurrent.TimeUnit;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import com.newrelic.agent.stats.StatsEngine;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.management.GarbageCollectorMXBean;
import com.newrelic.agent.config.CircuitBreakerConfig;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class CircuitBreakerService extends AbstractService implements HarvestListener, AgentConfigListener
{
    private static final int TRACER_SAMPLING_RATE = 1000;
    private volatile int tripped;
    private final CircuitBreakerConfig circuitBreakerConfig;
    private volatile GarbageCollectorMXBean oldGenGCBeanCached;
    private final ReentrantLock lock;
    private final ConcurrentMap<String, Boolean> missingData;
    private final ThreadLocal<Boolean> logWarning;
    private final ThreadLocal<Long> lastTotalGCTimeNS;
    private final ThreadLocal<Long> lastTotalCpuTimeNS;
    private final ThreadLocal<SamplingCounter> tracerSamplerCounter;
    
    public CircuitBreakerService() {
        super(CircuitBreakerService.class.getSimpleName());
        this.tripped = 0;
        this.oldGenGCBeanCached = null;
        this.lock = new ReentrantLock();
        this.logWarning = new ThreadLocal<Boolean>() {
            protected Boolean initialValue() {
                return true;
            }
        };
        this.lastTotalGCTimeNS = new ThreadLocal<Long>() {
            protected Long initialValue() {
                return 0L;
            }
        };
        this.lastTotalCpuTimeNS = new ThreadLocal<Long>() {
            protected Long initialValue() {
                return 0L;
            }
        };
        this.tracerSamplerCounter = new ThreadLocal<SamplingCounter>() {
            protected SamplingCounter initialValue() {
                return CircuitBreakerService.createTracerSamplerCounter();
            }
        };
        this.circuitBreakerConfig = ServiceFactory.getConfigService().getDefaultAgentConfig().getCircuitBreakerConfig();
        if (this.isEnabled() && (null == this.getOldGenGCBean() || this.getCpuTimeNS() < 0L)) {
            Agent.LOG.log(Level.WARNING, "Circuit breaker: Missing required JMX beans. Cannot enable circuit breaker. GC bean: {0} CpuTime: {1}", new Object[] { this.getOldGenGCBean(), this.getCpuTimeNS() });
            this.circuitBreakerConfig.updateEnabled(false);
        }
        ServiceFactory.getConfigService().addIAgentConfigListener(this);
        this.missingData = new ConcurrentHashMap<String, Boolean>();
    }
    
    public boolean isEnabled() {
        return this.circuitBreakerConfig.isEnabled();
    }
    
    protected void doStart() throws Exception {
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getConfigService().removeIAgentConfigListener(this);
        ServiceFactory.getHarvestService().removeHarvestListener(this);
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        this.lastTotalCpuTimeNS.set(this.getCpuTimeNS());
        this.lastTotalGCTimeNS.set(this.getGCCpuTimeNS());
        if (this.missingData.containsKey(appName) && this.missingData.get(appName)) {
            this.recordBreakerOnMetrics(statsEngine, "AgentCheck/CircuitBreaker/tripped/memory");
        }
        else {
            this.recordBreakerOffMetrics(statsEngine);
        }
    }
    
    private void recordBreakerOnMetrics(final StatsEngine statsEngine, final String tripCauseMetric) {
        statsEngine.getStats("AgentCheck/CircuitBreaker/tripped/all").incrementCallCount();
        statsEngine.getStats(tripCauseMetric).incrementCallCount();
    }
    
    private void recordBreakerOffMetrics(final StatsEngine statsEngine) {
        statsEngine.recordEmptyStats("AgentCheck/CircuitBreaker/tripped/all");
    }
    
    public void afterHarvest(final String appName) {
        if (this.isTripped() && this.shouldReset()) {
            this.reset();
        }
        if (!this.isTripped()) {
            this.missingData.put(appName, false);
            if (this.isTripped()) {
                this.missingData.put(appName, true);
            }
        }
    }
    
    private boolean shouldTrip() {
        if (!this.isEnabled()) {
            return false;
        }
        final long cpuTime = this.getCpuTimeNS() - this.lastTotalCpuTimeNS.get();
        final long gcCpuTime = this.getGCCpuTimeNS() - this.lastTotalGCTimeNS.get();
        final long totalTime = cpuTime + gcCpuTime;
        final double gcCpuTimePercentage = gcCpuTime / totalTime * 100.0;
        if (cpuTime <= 0L) {
            return false;
        }
        final double percentageFreeMemory = 100.0 * ((Runtime.getRuntime().freeMemory() + (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory())) / Runtime.getRuntime().maxMemory());
        this.lastTotalCpuTimeNS.set(this.lastTotalCpuTimeNS.get() + cpuTime);
        this.lastTotalGCTimeNS.set(this.lastTotalGCTimeNS.get() + gcCpuTime);
        final int freeMemoryThreshold = this.circuitBreakerConfig.getMemoryThreshold();
        final int gcCPUThreshold = this.circuitBreakerConfig.getGcCpuThreshold();
        Agent.LOG.log(Level.FINEST, "Circuit breaker: percentage free memory {0}%  GC CPU time percentage {1}% (freeMemoryThreshold {2}, gcCPUThreshold {3})", new Object[] { percentageFreeMemory, gcCpuTimePercentage, freeMemoryThreshold, gcCPUThreshold });
        if (gcCpuTimePercentage >= gcCPUThreshold && percentageFreeMemory <= freeMemoryThreshold) {
            Agent.LOG.log(Level.FINE, "Circuit breaker tripped at memory {0}%  GC CPU time {1}%", new Object[] { percentageFreeMemory, gcCpuTimePercentage });
            return true;
        }
        return false;
    }
    
    private boolean shouldReset() {
        return !this.shouldTrip();
    }
    
    public boolean isTripped() {
        if (this.tracerSamplerCounter.get().shouldSample() && this.tripped == 0) {
            this.checkAndTrip();
        }
        return this.tripped == 1;
    }
    
    private void trip() {
        this.tripped = 1;
        for (final String appName : this.missingData.keySet()) {
            this.missingData.put(appName, true);
        }
        if (this.logWarning.get()) {
            this.logWarning.set(false);
            Agent.LOG.log(Level.WARNING, "Circuit breaker tripped. The agent ceased to create transaction data to perserve heap memory. This may cause incomplete transaction data in the APM UI.");
        }
    }
    
    public void reset() {
        this.tripped = 0;
        Agent.LOG.log(Level.FINE, "Circuit breaker reset");
        this.logWarning.set(true);
    }
    
    public boolean checkAndTrip() {
        if (this.lock.tryLock()) {
            try {
                if (!this.isTripped() && this.shouldTrip()) {
                    this.trip();
                    final boolean b = true;
                    this.lock.unlock();
                    return b;
                }
                this.lock.unlock();
            }
            finally {
                this.lock.unlock();
            }
        }
        return false;
    }
    
    private long getCpuTimeNS() {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        if (!threadMXBean.isThreadCpuTimeSupported() || !threadMXBean.isThreadCpuTimeEnabled()) {
            return -1L;
        }
        long cpuTime = 0L;
        long threadCpuTime = 0L;
        for (final long id : threadMXBean.getAllThreadIds()) {
            threadCpuTime = threadMXBean.getThreadCpuTime(id);
            if (threadCpuTime != -1L) {
                cpuTime += threadCpuTime;
            }
        }
        return cpuTime;
    }
    
    private long getGCCpuTimeNS() {
        return TimeUnit.NANOSECONDS.convert(this.getOldGenGCBean().getCollectionTime(), TimeUnit.MILLISECONDS);
    }
    
    private long getGCCount() {
        long gcCpuCount = 0L;
        long collectorCount = 0L;
        for (final GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            collectorCount = gcBean.getCollectionCount();
            if (collectorCount != -1L) {
                gcCpuCount += gcBean.getCollectionCount();
            }
        }
        return gcCpuCount;
    }
    
    private GarbageCollectorMXBean getOldGenGCBean() {
        if (null != this.oldGenGCBeanCached) {
            return this.oldGenGCBeanCached;
        }
        synchronized (this) {
            if (null != this.oldGenGCBeanCached) {
                return this.oldGenGCBeanCached;
            }
            GarbageCollectorMXBean lowestGCCountBean = null;
            Agent.LOG.log(Level.FINEST, "Circuit breaker: looking for old gen gc bean");
            boolean tie = false;
            final long totalGCs = this.getGCCount();
            for (final GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                Agent.LOG.log(Level.FINEST, "Circuit breaker: checking {0}", new Object[] { gcBean.getName() });
                if (null == lowestGCCountBean || lowestGCCountBean.getCollectionCount() > gcBean.getCollectionCount()) {
                    tie = false;
                    lowestGCCountBean = gcBean;
                }
                else {
                    if (lowestGCCountBean.getCollectionCount() != gcBean.getCollectionCount()) {
                        continue;
                    }
                    tie = true;
                }
            }
            if (this.getGCCount() == totalGCs && !tie) {
                Agent.LOG.log(Level.FINEST, "Circuit breaker: found and cached oldGenGCBean: {0}", new Object[] { lowestGCCountBean.getName() });
                return this.oldGenGCBeanCached = lowestGCCountBean;
            }
            Agent.LOG.log(Level.FINEST, "Circuit breaker: unable to find oldGenGCBean. Best guess: {0}", new Object[] { lowestGCCountBean.getName() });
            return lowestGCCountBean;
        }
    }
    
    public void configChanged(final String appName, final AgentConfig agentConfig) {
        final int newGCCpuThreshold = agentConfig.getCircuitBreakerConfig().getGcCpuThreshold();
        final int newMemoryThreshold = agentConfig.getCircuitBreakerConfig().getMemoryThreshold();
        final boolean newEnabled = agentConfig.getCircuitBreakerConfig().isEnabled();
        if (newGCCpuThreshold == this.circuitBreakerConfig.getGcCpuThreshold() && newMemoryThreshold == this.circuitBreakerConfig.getMemoryThreshold() && newEnabled == this.circuitBreakerConfig.isEnabled()) {
            return;
        }
        this.circuitBreakerConfig.updateEnabled(newEnabled);
        this.circuitBreakerConfig.updateThresholds(newGCCpuThreshold, newMemoryThreshold);
        Agent.LOG.log(Level.INFO, "Circuit breaker: updated configuration - enabled {0} GC CPU Threshold {1}% Memory Threshold {2}%.", new Object[] { this.circuitBreakerConfig.isEnabled(), this.circuitBreakerConfig.getGcCpuThreshold(), this.circuitBreakerConfig.getMemoryThreshold() });
    }
    
    public void addRPMService(final RPMService rpmService) {
        this.missingData.put(rpmService.getApplicationName(), this.isTripped());
    }
    
    public void removeRPMService(final RPMService rpmService) {
        this.missingData.remove(rpmService.getApplicationName());
    }
    
    public void setPreviousChecksForTesting(final long newGCTimeNS, final long newCpuTimeNS) {
        this.lastTotalGCTimeNS.set(newGCTimeNS);
        this.lastTotalCpuTimeNS.set(newCpuTimeNS);
    }
    
    public static SamplingCounter createTracerSamplerCounter() {
        return new SamplingCounter(1000L);
    }
}
