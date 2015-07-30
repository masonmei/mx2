// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.util.TimeConversion;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.Agent;
import java.lang.management.ManagementFactory;

public abstract class AbstractCPUSampler
{
    private double lastCPUTimeSeconds;
    private long lastTimestampNanos;
    private final int processorCount;
    
    protected AbstractCPUSampler() {
        this.processorCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        Agent.LOG.finer(this.processorCount + " processor(s)");
    }
    
    protected abstract double getProcessCpuTime();
    
    protected void recordCPU(final StatsEngine statsEngine) {
        final double currentProcessTime = this.getProcessCpuTime();
        final double dCPU = currentProcessTime - this.lastCPUTimeSeconds;
        this.lastCPUTimeSeconds = currentProcessTime;
        final long now = System.nanoTime();
        final long elapsedNanos = now - this.lastTimestampNanos;
        this.lastTimestampNanos = now;
        final double elapsedTime = TimeConversion.convertNanosToSeconds(elapsedNanos);
        final double utilization = dCPU / (elapsedTime * this.processorCount);
        final boolean shouldLog = Agent.LOG.isLoggable(Level.FINER);
        if (shouldLog) {
            final String msg = MessageFormat.format("Recorded CPU time: {0} ({1}) {2}", dCPU, utilization, this.getClass().getName());
            Agent.LOG.finer(msg);
        }
        if (this.lastCPUTimeSeconds > 0.0 && dCPU >= 0.0) {
            if (Double.isNaN(dCPU) || Double.isInfinite(dCPU)) {
                if (shouldLog) {
                    final String msg = MessageFormat.format("Infinite or non-number CPU time: {0} (current) - {1} (last)", currentProcessTime, this.lastCPUTimeSeconds);
                    Agent.LOG.finer(msg);
                }
            }
            else {
                statsEngine.getStats("CPU/User Time").recordDataPoint((float)dCPU);
            }
            if (Double.isNaN(utilization) || Double.isInfinite(utilization)) {
                if (shouldLog) {
                    final String msg = MessageFormat.format("Infinite or non-number CPU utilization: {0} ({1})", utilization, dCPU);
                    Agent.LOG.finer(msg);
                }
            }
            else {
                statsEngine.getStats("CPU/User/Utilization").recordDataPoint((float)utilization);
            }
        }
        else if (shouldLog) {
            final String msg = MessageFormat.format("Bad CPU time: {0} (current) - {1} (last)", currentProcessTime, this.lastCPUTimeSeconds);
            Agent.LOG.finer(msg);
        }
    }
}
