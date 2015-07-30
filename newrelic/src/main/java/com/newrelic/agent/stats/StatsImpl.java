// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

public class StatsImpl extends AbstractStats implements Stats
{
    private float total;
    private float minValue;
    private float maxValue;
    private double sumOfSquares;
    
    protected StatsImpl() {
    }
    
    public StatsImpl(final int count, final float total, final float minValue, final float maxValue, final double sumOfSquares) {
        super(count);
        this.total = total;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sumOfSquares = sumOfSquares;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final StatsImpl newStats = new StatsImpl();
        newStats.count = this.count;
        newStats.total = this.total;
        newStats.minValue = this.minValue;
        newStats.maxValue = this.maxValue;
        newStats.sumOfSquares = this.sumOfSquares;
        return newStats;
    }
    
    public String toString() {
        return super.toString() + " [tot=" + this.total + ", min=" + this.minValue + ", maxV=" + this.maxValue + "]";
    }
    
    public void recordDataPoint(final float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            throw new IllegalArgumentException("Data points must be numbers");
        }
        final double sos = this.sumOfSquares + value * value;
        if (sos < this.sumOfSquares) {
            throw new IllegalArgumentException("Data value " + value + " caused sum of squares to roll over");
        }
        if (this.count > 0) {
            this.minValue = Math.min(value, this.minValue);
        }
        else {
            this.minValue = value;
        }
        ++this.count;
        this.total += value;
        this.maxValue = Math.max(value, this.maxValue);
        this.sumOfSquares = sos;
    }
    
    public boolean hasData() {
        return this.count > 0 || this.total > 0.0f;
    }
    
    public void reset() {
        this.count = 0;
        final float total = 0.0f;
        this.maxValue = total;
        this.minValue = total;
        this.total = total;
        this.sumOfSquares = 0.0;
    }
    
    public float getTotal() {
        return this.total;
    }
    
    public float getTotalExclusiveTime() {
        return this.total;
    }
    
    public float getMinCallTime() {
        return this.minValue;
    }
    
    public float getMaxCallTime() {
        return this.maxValue;
    }
    
    public double getSumOfSquares() {
        return this.sumOfSquares;
    }
    
    public void merge(final StatsBase statsObj) {
        if (statsObj instanceof StatsImpl) {
            final StatsImpl stats = (StatsImpl)statsObj;
            if (stats.count > 0) {
                if (this.count > 0) {
                    this.minValue = Math.min(this.minValue, stats.minValue);
                }
                else {
                    this.minValue = stats.minValue;
                }
            }
            this.count += stats.count;
            this.total += stats.total;
            this.maxValue = Math.max(this.maxValue, stats.maxValue);
            this.sumOfSquares += stats.sumOfSquares;
        }
    }
}
