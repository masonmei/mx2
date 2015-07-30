// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.circuitbreaker;

public class SamplingCounter
{
    private final long samplingRate;
    private long count;
    
    public SamplingCounter(final long samplingRate) {
        this.count = 0L;
        this.samplingRate = samplingRate;
    }
    
    public boolean shouldSample() {
        final long count = this.count + 1L;
        this.count = count;
        if (count > this.samplingRate) {
            this.count = 0L;
            return true;
        }
        return false;
    }
}
