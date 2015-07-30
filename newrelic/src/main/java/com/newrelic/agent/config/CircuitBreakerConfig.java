// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Map;

public class CircuitBreakerConfig extends BaseConfig
{
    public static final String ENABLED = "enabled";
    public static final Boolean DEFAULT_ENABLED;
    public static final String MEMORY_THRESHOLD = "memory_threshold";
    public static final int DEFAULT_MEMORY_THRESHOLD = 20;
    public static final String GC_CPU_THRESHOLD = "gc_cpu_threshold";
    public static final int DEFAULT_GC_CPU_THRESHOLD = 10;
    public static final String PROPERTY_NAME = "circuitbreaker";
    public static final String PROPERTY_ROOT = "newrelic.config.circuitbreaker.";
    private boolean isEnabled;
    private int memoryThreshold;
    private int gcCpuThreshold;
    
    public CircuitBreakerConfig(final Map<String, Object> pProps) {
        super(pProps, "newrelic.config.circuitbreaker.");
        this.isEnabled = this.getProperty("enabled", CircuitBreakerConfig.DEFAULT_ENABLED);
        this.memoryThreshold = this.getProperty("memory_threshold", 20);
        this.gcCpuThreshold = this.getProperty("gc_cpu_threshold", 10);
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public int getMemoryThreshold() {
        return this.memoryThreshold;
    }
    
    public int getGcCpuThreshold() {
        return this.gcCpuThreshold;
    }
    
    public boolean updateThresholds(final int newGCCpuThreshold, final int newMemoryThreshold) {
        if (newGCCpuThreshold >= 0 && newMemoryThreshold >= 0) {
            this.gcCpuThreshold = newGCCpuThreshold;
            this.memoryThreshold = newMemoryThreshold;
            return true;
        }
        return false;
    }
    
    public boolean updateEnabled(final boolean newEnabled) {
        if (this.isEnabled != newEnabled) {
            this.isEnabled = newEnabled;
            return true;
        }
        return false;
    }
    
    static {
        DEFAULT_ENABLED = Boolean.TRUE;
    }
}
