// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

public class InvocationGate
{
    private static final int MAX_MASK = 65535;
    private volatile long mask;
    private volatile long lastMaskCheck;
    private long invocationCounter;
    private static final long thresholdForMaskIncrease = 100L;
    private final long thresholdForMaskDecrease = 800L;
    
    public InvocationGate() {
        this.mask = 15L;
        this.lastMaskCheck = System.currentTimeMillis();
        this.invocationCounter = 0L;
    }
    
    public boolean skipFurtherWork() {
        return (this.invocationCounter++ & this.mask) != this.mask;
    }
    
    public void updateMaskIfNecessary(final long now) {
        final long timeElapsedSinceLastMaskUpdateCheck = now - this.lastMaskCheck;
        this.lastMaskCheck = now;
        if (timeElapsedSinceLastMaskUpdateCheck < 100L && this.mask < 65535L) {
            this.mask = (this.mask << 1 | 0x1L);
        }
        else if (timeElapsedSinceLastMaskUpdateCheck > 800L) {
            this.mask >>>= 2;
        }
    }
}
