// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.concurrent.atomic.AtomicLong;

public class XrayClockTimeController extends AbstractController
{
    private long startTimeInNanos;
    private final AtomicLong runTime;
    
    public XrayClockTimeController(final ProfilingTask profilingTask) {
        super(profilingTask);
        this.runTime = new AtomicLong();
    }
    
    public void run() {
        final long startTime = System.nanoTime();
        super.run();
        this.runTime.addAndGet(System.nanoTime() - startTime);
    }
    
    protected int doCalculateSamplePeriodInMillis() {
        final long runTimeInNanos = this.getAndResetRunTimeInNanos();
        final long endTimeInNanos = this.getTimeInNanos();
        int samplePeriod = this.getSamplePeriodInMillis();
        if (this.startTimeInNanos > 0L) {
            final long timeInNanos = endTimeInNanos - this.startTimeInNanos;
            samplePeriod = this.calculateSamplePeriodInMillis(timeInNanos, runTimeInNanos);
        }
        this.startTimeInNanos = endTimeInNanos;
        return samplePeriod;
    }
    
    private int calculateSamplePeriodInMillis(final long timeInNanos, final long runTimeInNanos) {
        if (runTimeInNanos == 0L || timeInNanos == 0L) {
            return this.getSamplePeriodInMillis();
        }
        final float runUtilization = runTimeInNanos / (timeInNanos * this.getProcessorCount());
        return (int)(runUtilization * this.getSamplePeriodInMillis() / XrayClockTimeController.TARGET_UTILIZATION);
    }
    
    protected long getTimeInNanos() {
        return System.nanoTime();
    }
    
    protected long getAndResetRunTimeInNanos() {
        return this.runTime.getAndSet(0L);
    }
}
