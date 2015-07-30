// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.Agent;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.MetricAggregator;

public abstract class AbstractMetricAggregator implements MetricAggregator
{
    private final Logger logger;
    
    protected AbstractMetricAggregator() {
        this((Logger)Agent.LOG);
    }
    
    protected AbstractMetricAggregator(final Logger logger) {
        this.logger = logger;
    }
    
    public final void recordResponseTimeMetric(final String name, final long totalTime, final long exclusiveTime, final TimeUnit timeUnit) {
        if (name == null || name.length() == 0) {
            this.logger.log(Level.FINER, "recordResponseTimeMetric was invoked with a null or empty name", new Object[0]);
            return;
        }
        try {
            this.doRecordResponseTimeMetric(name, totalTime, exclusiveTime, timeUnit);
            this.logger.log(Level.FINER, "Recorded response time metric \"{0}\": {1}", new Object[] { name, totalTime });
        }
        catch (Throwable t) {
            logException(this.logger, t, "Exception recording response time metric \"{0}\": {1}", name, t);
        }
    }
    
    protected abstract void doRecordResponseTimeMetric(final String p0, final long p1, final long p2, final TimeUnit p3);
    
    public final void recordMetric(final String name, final float value) {
        if (name == null || name.length() == 0) {
            this.logger.log(Level.FINER, "recordMetric was invoked with a null or empty name", new Object[0]);
            return;
        }
        try {
            this.doRecordMetric(name, value);
            this.logger.log(Level.FINER, "Recorded metric \"{0}\": {1}", new Object[] { name, value });
        }
        catch (Throwable t) {
            logException(this.logger, t, "Exception recording metric \"{0}\": {1}", name, t);
        }
    }
    
    protected abstract void doRecordMetric(final String p0, final float p1);
    
    public final void recordResponseTimeMetric(final String name, final long millis) {
        this.recordResponseTimeMetric(name, millis, millis, TimeUnit.MILLISECONDS);
    }
    
    public final void incrementCounter(final String name) {
        this.incrementCounter(name, 1);
    }
    
    public final void incrementCounter(final String name, final int count) {
        if (name == null || name.length() == 0) {
            this.logger.log(Level.FINER, "incrementCounter was invoked with a null metric name", new Object[0]);
            return;
        }
        try {
            this.doIncrementCounter(name, count);
            this.logger.log(Level.FINER, "incremented counter \"{0}\": {1}", new Object[] { name, count });
        }
        catch (Throwable t) {
            logException(this.logger, t, "Exception incrementing counter \"{0}\",{1} : {2}", name, count, t);
        }
    }
    
    protected abstract void doIncrementCounter(final String p0, final int p1);
    
    private static void logException(final Logger logger, final Throwable t, final String pattern, final Object... parts) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, t, pattern, parts);
        }
        else if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, pattern, parts);
        }
    }
}
