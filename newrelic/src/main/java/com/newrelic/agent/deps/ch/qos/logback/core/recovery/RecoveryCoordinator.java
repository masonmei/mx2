// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.recovery;

public class RecoveryCoordinator
{
    public static final long BACKOFF_COEFFICIENT_MIN = 20L;
    static long BACKOFF_COEFFICIENT_MAX;
    private long backOffCoefficient;
    private static long UNSET;
    private long currentTime;
    long next;
    
    public RecoveryCoordinator() {
        this.backOffCoefficient = 20L;
        this.currentTime = RecoveryCoordinator.UNSET;
        this.next = System.currentTimeMillis() + this.getBackoffCoefficient();
    }
    
    public boolean isTooSoon() {
        final long now = this.getCurrentTime();
        if (now > this.next) {
            this.next = now + this.getBackoffCoefficient();
            return false;
        }
        return true;
    }
    
    void setCurrentTime(final long forcedTime) {
        this.currentTime = forcedTime;
    }
    
    private long getCurrentTime() {
        if (this.currentTime != RecoveryCoordinator.UNSET) {
            return this.currentTime;
        }
        return System.currentTimeMillis();
    }
    
    private long getBackoffCoefficient() {
        final long currentCoeff = this.backOffCoefficient;
        if (this.backOffCoefficient < RecoveryCoordinator.BACKOFF_COEFFICIENT_MAX) {
            this.backOffCoefficient *= 4L;
        }
        return currentCoeff;
    }
    
    static {
        RecoveryCoordinator.BACKOFF_COEFFICIENT_MAX = 327680L;
        RecoveryCoordinator.UNSET = -1L;
    }
}
