// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deadlock;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import com.newrelic.agent.errors.DeadlockTraceError;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.errors.ErrorService;
import com.newrelic.agent.errors.TracedError;
import java.util.List;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class DeadLockDetector
{
    private static final int MAX_THREAD_DEPTH = 300;
    private final ThreadMXBean threadMXBean;
    
    public DeadLockDetector() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }
    
    protected void detectDeadlockedThreads() {
        final ThreadInfo[] threadInfos = this.getDeadlockedThreadInfos();
        if (threadInfos.length > 0) {
            Agent.LOG.info(MessageFormat.format("Detected {0} deadlocked threads", threadInfos.length));
            if (Agent.isDebugEnabled()) {
                boolean harvestThreadLocked = false;
                for (final ThreadInfo threadInfo : threadInfos) {
                    if (threadInfo.getThreadName().equals("New Relic Harvest Service")) {
                        harvestThreadLocked = true;
                    }
                }
                if (harvestThreadLocked) {
                    Agent.LOG.severe("A harvest thread deadlock condition was detected");
                    return;
                }
            }
            this.reportDeadlocks(Arrays.asList(threadInfos));
        }
    }
    
    ThreadInfo[] getDeadlockedThreadInfos() {
        final long[] deadlockedThreadIds = this.findDeadlockedThreads();
        if (deadlockedThreadIds == null) {
            return new ThreadInfo[0];
        }
        return this.threadMXBean.getThreadInfo(deadlockedThreadIds, 300);
    }
    
    protected ThreadMXBean getThreadMXBean() {
        return this.threadMXBean;
    }
    
    protected long[] findDeadlockedThreads() {
        try {
            return this.getThreadMXBean().findDeadlockedThreads();
        }
        catch (UnsupportedOperationException e) {
            return this.getThreadMXBean().findMonitorDeadlockedThreads();
        }
    }
    
    private void reportDeadlocks(final List<ThreadInfo> deadThreads) {
        final TracedError[] tracedErrors = this.getTracedErrors(deadThreads);
        this.getErrorService().reportErrors(tracedErrors);
    }
    
    private ErrorService getErrorService() {
        return ServiceFactory.getRPMService().getErrorService();
    }
    
    TracedError[] getTracedErrors(final List<ThreadInfo> threadInfos) {
        final Map<Long, ThreadInfo> idToThreads = new HashMap<Long, ThreadInfo>();
        for (final ThreadInfo thread : threadInfos) {
            idToThreads.put(thread.getThreadId(), thread);
        }
        final List<TracedError> errors = new ArrayList<TracedError>();
        final Set<Long> skipIds = new HashSet<Long>();
        for (final ThreadInfo thread2 : threadInfos) {
            if (!skipIds.contains(thread2.getThreadId())) {
                final long otherId = thread2.getLockOwnerId();
                skipIds.add(otherId);
                final ThreadInfo otherThread = idToThreads.get(otherId);
                final Map<String, String> parameters = (Map<String, String>)Maps.newHashMapWithExpectedSize(4);
                parameters.put("jvm.thread_name", thread2.getThreadName());
                final Map<String, StackTraceElement[]> stackTraces = new HashMap<String, StackTraceElement[]>();
                stackTraces.put(thread2.getThreadName(), thread2.getStackTrace());
                if (otherThread != null) {
                    parameters.put("jvm.lock_thread_name", otherThread.getThreadName());
                    stackTraces.put(otherThread.getThreadName(), otherThread.getStackTrace());
                }
                errors.add(new DeadlockTraceError(null, thread2, stackTraces, parameters));
            }
        }
        return errors.toArray(new TracedError[0]);
    }
}
