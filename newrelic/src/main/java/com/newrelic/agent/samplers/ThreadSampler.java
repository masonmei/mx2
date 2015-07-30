// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import java.security.AccessControlException;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.stats.StatsEngine;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class ThreadSampler implements MetricSampler
{
    private final ThreadMXBean threadMXBean;
    
    public ThreadSampler() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }
    
    public void sample(final StatsEngine statsEngine) {
        final int threadCount = this.threadMXBean.getThreadCount();
        statsEngine.getStats("Threads/all").setCallCount(threadCount);
        long[] deadlockedThreadIds;
        try {
            deadlockedThreadIds = this.threadMXBean.findMonitorDeadlockedThreads();
        }
        catch (AccessControlException e) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("An error occurred calling ThreadMXBean.findMonitorDeadlockedThreads: {0}", e);
                Agent.LOG.warning(msg);
            }
            deadlockedThreadIds = new long[0];
        }
        final int deadlockCount = (deadlockedThreadIds == null) ? 0 : deadlockedThreadIds.length;
        statsEngine.getStats("Threads/Deadlocks/all").setCallCount(deadlockCount);
    }
}
