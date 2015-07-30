// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import java.io.Writer;
import java.util.List;

public abstract class AbstractStats implements CountStats
{
    private static final List<Number> ZERO_ARRAY_LIST;
    protected int count;
    public static final StatsBase EMPTY_STATS;
    
    public AbstractStats() {
    }
    
    public AbstractStats(final int count) {
        this.count = count;
    }
    
    public void incrementCallCount(final int value) {
        this.count += value;
    }
    
    public void incrementCallCount() {
        ++this.count;
    }
    
    public int getCallCount() {
        return this.count;
    }
    
    public void setCallCount(final int count) {
        this.count = count;
    }
    
    public final void writeJSONString(final Writer writer) throws IOException, InvalidStatsException {
        List<Number> list;
        if (this.count < 0) {
            list = AbstractStats.ZERO_ARRAY_LIST;
        }
        else {
            list = Arrays.asList(this.count, this.getTotal(), this.getTotalExclusiveTime(), this.getMinCallTime(), this.getMaxCallTime(), this.getSumOfSquares());
        }
        JSONArray.writeJSONString(list, writer);
    }
    
    public abstract Object clone() throws CloneNotSupportedException;
    
    static {
        final Number zero = 0;
        ZERO_ARRAY_LIST = Arrays.asList(zero, zero, zero, zero, zero, zero);
        EMPTY_STATS = new StatsBase() {
            public boolean hasData() {
                return true;
            }
            
            public void merge(final StatsBase stats) {
            }
            
            public void reset() {
            }
            
            public Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
            
            public void writeJSONString(final Writer writer) throws IOException {
                JSONArray.writeJSONString(AbstractStats.ZERO_ARRAY_LIST, writer);
            }
        };
    }
}
