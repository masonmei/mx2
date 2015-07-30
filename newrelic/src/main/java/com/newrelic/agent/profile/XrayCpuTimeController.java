// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;

public class XrayCpuTimeController extends AbstractController implements ProfilingTaskController
{
    private long threadId;
    private long startCpuTimeInNanos;
    private long startTimeInNanos;
    private volatile long currentThreadId;
    
    public XrayCpuTimeController(final ProfilingTask profilingTask) {
        super(profilingTask);
        this.currentThreadId = Thread.currentThread().getId();
    }
    
    public void run() {
        this.currentThreadId = Thread.currentThread().getId();
        super.run();
    }
    
    protected int doCalculateSamplePeriodInMillis() {
        int samplePeriod = this.getSamplePeriodInMillis();
        final long nThreadId = this.getCurrentThreadId();
        final long oThreadId = this.threadId;
        this.threadId = nThreadId;
        long endCpuTimeInNanos;
        try {
            endCpuTimeInNanos = this.getThreadCpuTimeInNanos();
        }
        catch (Throwable t) {
            Agent.LOG.fine(MessageFormat.format("Error getting thread cpu time: {0}", t));
            return samplePeriod;
        }
        final long endTimeInNanos = this.getTimeInNanos();
        if (oThreadId == nThreadId) {
            samplePeriod = this.calculateSamplePeriod(endTimeInNanos - this.startTimeInNanos, endCpuTimeInNanos - this.startCpuTimeInNanos);
        }
        this.startCpuTimeInNanos = endCpuTimeInNanos;
        this.startTimeInNanos = endTimeInNanos;
        return samplePeriod;
    }
    
    private int calculateSamplePeriod(final long timeInNanos, final long cpuTimeInNanos) {
        if (cpuTimeInNanos == 0L || timeInNanos == 0L) {
            return this.getSamplePeriodInMillis();
        }
        final float cpuUtilization = cpuTimeInNanos / (timeInNanos * this.getProcessorCount());
        return (int)(cpuUtilization * this.getSamplePeriodInMillis() / XrayCpuTimeController.TARGET_UTILIZATION);
    }
    
    protected long getThreadCpuTimeInNanos() {
        return ManagementFactory.getThreadMXBean().getThreadCpuTime(this.threadId);
    }
    
    protected long getCurrentThreadId() {
        return this.currentThreadId;
    }
    
    protected long getTimeInNanos() {
        return System.nanoTime();
    }
}
