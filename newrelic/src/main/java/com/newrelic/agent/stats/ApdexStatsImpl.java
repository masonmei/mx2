// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import java.io.Writer;

public class ApdexStatsImpl implements ApdexStats
{
    private static final Integer ZERO;
    private int satisfying;
    private int tolerating;
    private int frustrating;
    private long apdexTInMillis;
    
    protected ApdexStatsImpl() {
        this.apdexTInMillis = ApdexStatsImpl.ZERO;
    }
    
    public ApdexStatsImpl(final int s, final int t, final int f) {
        this.apdexTInMillis = ApdexStatsImpl.ZERO;
        this.satisfying = s;
        this.tolerating = t;
        this.frustrating = f;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final ApdexStatsImpl newStats = new ApdexStatsImpl();
        newStats.frustrating = this.frustrating;
        newStats.satisfying = this.satisfying;
        newStats.tolerating = this.tolerating;
        return newStats;
    }
    
    public String toString() {
        return super.toString() + " [s=" + this.satisfying + ", t=" + this.tolerating + ", f=" + this.frustrating + "]";
    }
    
    public void recordApdexFrustrated() {
        ++this.frustrating;
    }
    
    public int getApdexSatisfying() {
        return this.satisfying;
    }
    
    public int getApdexTolerating() {
        return this.tolerating;
    }
    
    public int getApdexFrustrating() {
        return this.frustrating;
    }
    
    public void recordApdexResponseTime(final long responseTimeMillis, final long apdexTInMillis) {
        this.apdexTInMillis = apdexTInMillis;
        final ApdexPerfZone perfZone = ApdexPerfZone.getZone(responseTimeMillis, apdexTInMillis);
        switch (perfZone) {
            case SATISFYING: {
                ++this.satisfying;
                break;
            }
            case TOLERATING: {
                ++this.tolerating;
                break;
            }
            case FRUSTRATING: {
                this.recordApdexFrustrated();
                break;
            }
        }
    }
    
    public boolean hasData() {
        return this.satisfying > 0 || this.tolerating > 0 || this.frustrating > 0;
    }
    
    public void reset() {
        this.satisfying = 0;
        this.tolerating = 0;
        this.frustrating = 0;
    }
    
    public void writeJSONString(final Writer writer) throws IOException {
        final double apdexT = Long.valueOf(this.apdexTInMillis) / 1000.0;
        final List<Number> data = Arrays.asList(this.satisfying, this.tolerating, this.frustrating, apdexT, apdexT, ApdexStatsImpl.ZERO);
        JSONArray.writeJSONString(data, writer);
    }
    
    public void merge(final StatsBase statsObj) {
        if (statsObj instanceof ApdexStatsImpl) {
            final ApdexStatsImpl stats = (ApdexStatsImpl)statsObj;
            this.satisfying += stats.satisfying;
            this.tolerating += stats.tolerating;
            this.frustrating += stats.frustrating;
        }
    }
    
    static {
        ZERO = 0;
    }
}
