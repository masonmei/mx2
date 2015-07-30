// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.lang.management.ThreadMXBean;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashSet;
import com.newrelic.agent.util.StackTraces;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.lang.management.ThreadInfo;

public class ProfileSampler
{
    public static final int MAX_STACK_DEPTH = 300;
    private static final ThreadInfo[] EMPTY_THREAD_INFO_ARRAY;
    
    public void sampleStackTraces(final List<IProfile> profiles) {
        if (profiles.isEmpty()) {
            return;
        }
        final Set<Long> runningThreadIds = this.getRunningThreadIds();
        final Set<Long> requestThreadIds = this.getRequestThreadIds(runningThreadIds);
        ThreadInfo[] requestThreadInfos = null;
        for (final IProfile profile : profiles) {
            profile.beforeSampling();
            if (profile.getProfilerParameters().isOnlyRequestThreads()) {
                if (requestThreadInfos == null) {
                    requestThreadInfos = this.getRequestThreadInfos(requestThreadIds);
                }
                this.addRequestInfos(profile, requestThreadInfos);
            }
            else {
                this.addThreadInfos(profile, runningThreadIds, requestThreadIds, this.getAllThreadInfos());
            }
        }
    }
    
    private void addRequestInfos(final IProfile profile, final ThreadInfo[] threadInfos) {
        final RunnableThreadRules runnableThreadRules = new RunnableThreadRules();
        for (final ThreadInfo threadInfo : threadInfos) {
            if (threadInfo != null) {
                final boolean isRunnable = runnableThreadRules.isRunnable(threadInfo);
                profile.addStackTrace(threadInfo.getThreadId(), isRunnable, ThreadType.BasicThreadType.REQUEST, threadInfo.getStackTrace());
            }
        }
    }
    
    private void addThreadInfos(final IProfile profiler, final Set<Long> runningThreadIds, final Set<Long> requestThreadIds, final ThreadInfo[] threadInfos) {
        if (threadInfos.length == 0) {
            return;
        }
        final Set<Long> backgroundThreadIds = this.getBackgroundThreadIds(runningThreadIds);
        final Set<Long> agentThreadIds = ServiceFactory.getThreadService().getAgentThreadIds();
        final RunnableThreadRules runnableThreadRules = new RunnableThreadRules();
        for (final ThreadInfo threadInfo : threadInfos) {
            if (threadInfo != null) {
                final boolean isRunnable = runnableThreadRules.isRunnable(threadInfo);
                if (isRunnable || !profiler.getProfilerParameters().isRunnablesOnly()) {
                    final long threadId = threadInfo.getThreadId();
                    ThreadType type;
                    if (agentThreadIds.contains(threadId)) {
                        type = ThreadType.BasicThreadType.AGENT;
                    }
                    else if (profiler.getProfilerParameters().isProfileAgentThreads() && StackTraces.isInAgentInstrumentation(threadInfo.getStackTrace())) {
                        type = ThreadType.BasicThreadType.AGENT_INSTRUMENTATION;
                    }
                    else if (requestThreadIds.contains(threadId)) {
                        type = ThreadType.BasicThreadType.REQUEST;
                    }
                    else if (backgroundThreadIds.contains(threadId)) {
                        type = ThreadType.BasicThreadType.BACKGROUND;
                    }
                    else {
                        type = ThreadType.BasicThreadType.OTHER;
                    }
                    profiler.addStackTrace(threadId, isRunnable, type, threadInfo.getStackTrace());
                }
            }
        }
    }
    
    private Set<Long> getRunningThreadIds() {
        return ServiceFactory.getTransactionService().getRunningThreadIds();
    }
    
    private Set<Long> getRequestThreadIds(final Set<Long> runningThreadIds) {
        final Set<Long> result = new HashSet<Long>(ServiceFactory.getThreadService().getRequestThreadIds());
        result.retainAll(runningThreadIds);
        return result;
    }
    
    private Set<Long> getBackgroundThreadIds(final Set<Long> runningThreadIds) {
        final Set<Long> result = new HashSet<Long>(ServiceFactory.getThreadService().getBackgroundThreadIds());
        result.retainAll(runningThreadIds);
        return result;
    }
    
    private ThreadInfo[] getAllThreadInfos() {
        long[] threadIds = this.getAllThreadIds();
        if (threadIds == null || threadIds.length == 0) {
            return ProfileSampler.EMPTY_THREAD_INFO_ARRAY;
        }
        final Set<Long> ids = new HashSet<Long>(threadIds.length);
        for (final long threadId : threadIds) {
            ids.add(threadId);
        }
        ids.remove(Thread.currentThread().getId());
        threadIds = this.convertToLongArray(ids);
        return this.getThreadInfos(threadIds);
    }
    
    private ThreadInfo[] getRequestThreadInfos(final Set<Long> requestThreadIds) {
        final long[] threadIds = this.convertToLongArray(requestThreadIds);
        return this.getThreadInfos(threadIds);
    }
    
    private long[] convertToLongArray(final Set<Long> ids) {
        final long[] arr = new long[ids.size()];
        int i = 0;
        for (final Long id : ids) {
            arr[i++] = id;
        }
        return arr;
    }
    
    private ThreadInfo[] getThreadInfos(final long[] threadIds) {
        try {
            final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            if (threadIds.length > 0) {
                return threadMXBean.getThreadInfo(threadIds, 300);
            }
        }
        catch (SecurityException e) {
            Agent.LOG.finer(MessageFormat.format("An error occurred getting thread info: {0}", e));
        }
        return ProfileSampler.EMPTY_THREAD_INFO_ARRAY;
    }
    
    private long[] getAllThreadIds() {
        try {
            final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            return threadMXBean.getAllThreadIds();
        }
        catch (SecurityException e) {
            Agent.LOG.finer(MessageFormat.format("An error occurred getting all thread ids: {0}", e));
            return null;
        }
    }
    
    static {
        EMPTY_THREAD_INFO_ARRAY = new ThreadInfo[0];
    }
}
