// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import com.newrelic.agent.stats.StatsEngine;
import java.lang.management.ManagementFactory;

public abstract class AbstractController implements ProfilingTaskController
{
    static int MAX_SAMPLE_PERIOD_IN_MILLIS;
    static int MIN_SAMPLE_PERIOD_IN_MILLIS;
    static float TARGET_UTILIZATION;
    private final ProfilingTask delegate;
    private final int processorCount;
    private int samplePeriodInMillis;
    
    public AbstractController(final ProfilingTask delegate) {
        this.samplePeriodInMillis = -1;
        this.delegate = delegate;
        this.processorCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
    }
    
    protected int getProcessorCount() {
        return this.processorCount;
    }
    
    abstract int doCalculateSamplePeriodInMillis();
    
    public int getSamplePeriodInMillis() {
        if (this.samplePeriodInMillis == -1) {
            return AbstractController.MIN_SAMPLE_PERIOD_IN_MILLIS;
        }
        return this.samplePeriodInMillis;
    }
    
    private void calculateSamplePeriodInMillis() {
        if (this.samplePeriodInMillis == -1) {
            return;
        }
        int nSamplePeriodInMillis = this.doCalculateSamplePeriodInMillis();
        if (nSamplePeriodInMillis > this.samplePeriodInMillis) {
            nSamplePeriodInMillis = this.samplePeriodInMillis * 2;
        }
        else if (nSamplePeriodInMillis <= this.samplePeriodInMillis / 4) {
            nSamplePeriodInMillis = this.samplePeriodInMillis / 2;
        }
        else {
            nSamplePeriodInMillis = this.samplePeriodInMillis;
        }
        nSamplePeriodInMillis = Math.min(AbstractController.MAX_SAMPLE_PERIOD_IN_MILLIS, Math.max(nSamplePeriodInMillis, AbstractController.MIN_SAMPLE_PERIOD_IN_MILLIS));
        this.samplePeriodInMillis = nSamplePeriodInMillis;
    }
    
    public void run() {
        this.delegate.run();
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        this.delegate.beforeHarvest(appName, statsEngine);
    }
    
    public void afterHarvest(final String appName) {
        this.calculateSamplePeriodInMillis();
        this.delegate.afterHarvest(appName);
    }
    
    public void addProfile(final ProfilerParameters parameters) {
        if (this.samplePeriodInMillis == -1) {
            this.samplePeriodInMillis = (Integer)(Object)parameters.getSamplePeriodInMillis();
        }
        this.delegate.addProfile(parameters);
    }
    
    public void removeProfile(final ProfilerParameters parameters) {
        this.delegate.removeProfile(parameters);
    }
    
    ProfilingTask getDelegate() {
        return this.delegate;
    }
    
    static {
        AbstractController.MAX_SAMPLE_PERIOD_IN_MILLIS = 6400;
        AbstractController.MIN_SAMPLE_PERIOD_IN_MILLIS = 100;
        AbstractController.TARGET_UTILIZATION = 0.02f;
    }
}
