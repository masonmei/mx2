// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.util.SafeWrappers;
import java.text.MessageFormat;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import com.newrelic.agent.service.AbstractService;

public class ThreadService extends AbstractService
{
    private static final float HASH_SET_LOAD_FACTOR = 0.75f;
    private static final String THREAD_SERVICE_THREAD_NAME = "New Relic Thread Service";
    private static final long INITIAL_DELAY_IN_SECONDS = 300L;
    private static final long SUBSEQUENT_DELAY_IN_SECONDS = 300L;
    private volatile ScheduledExecutorService scheduledExecutor;
    private volatile ScheduledFuture<?> deadThreadsTask;
    private final Map<Long, Boolean> agentThreadIds;
    private final Map<Long, Boolean> requestThreadIds;
    private final Map<Long, Boolean> backgroundThreadIds;
    private final ThreadMXBean threadMXBean;
    
    public ThreadService() {
        super(ThreadService.class.getSimpleName());
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.agentThreadIds = new ConcurrentHashMap<Long, Boolean>(6);
        this.requestThreadIds = new ConcurrentHashMap<Long, Boolean>();
        this.backgroundThreadIds = new ConcurrentHashMap<Long, Boolean>();
    }
    
    protected void doStart() {
        if (this.threadMXBean == null) {
            return;
        }
        final ThreadFactory threadFactory = new DefaultThreadFactory("New Relic Thread Service", true);
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        final Runnable runnable = new Runnable() {
            public void run() {
                try {
                    ThreadService.this.detectDeadThreads();
                }
                catch (Throwable t) {
                    final String msg = MessageFormat.format("Unexpected exception detecting dead threads: {0}", t.toString());
                    ThreadService.this.getLogger().warning(msg);
                }
            }
        };
        this.deadThreadsTask = this.scheduledExecutor.scheduleWithFixedDelay(SafeWrappers.safeRunnable(runnable), 300L, 300L, TimeUnit.SECONDS);
    }
    
    protected void doStop() {
        if (this.deadThreadsTask != null) {
            this.deadThreadsTask.cancel(false);
        }
        this.scheduledExecutor.shutdown();
    }
    
    protected void detectDeadThreads() {
        final long[] threadIds = this.threadMXBean.getAllThreadIds();
        final int hashSetSize = (int)(threadIds.length / 0.75f) + 1;
        final Set<Long> ids = new HashSet<Long>(hashSetSize);
        for (final long threadId : threadIds) {
            ids.add(threadId);
        }
        this.retainAll(this.requestThreadIds, ids);
        this.retainAll(this.backgroundThreadIds, ids);
    }
    
    private void retainAll(final Map<Long, Boolean> map, final Set<Long> ids) {
        for (final Map.Entry<Long, Boolean> entry : map.entrySet()) {
            if (!ids.contains(entry.getKey())) {
                map.remove(entry.getKey());
            }
        }
    }
    
    public Set<Long> getRequestThreadIds() {
        return Collections.unmodifiableSet((Set<? extends Long>)this.requestThreadIds.keySet());
    }
    
    public Set<Long> getBackgroundThreadIds() {
        return Collections.unmodifiableSet((Set<? extends Long>)this.backgroundThreadIds.keySet());
    }
    
    public void noticeRequestThread(final Long threadId) {
        this.requestThreadIds.put(threadId, Boolean.TRUE);
    }
    
    public void noticeBackgroundThread(final Long threadId) {
        this.backgroundThreadIds.put(threadId, Boolean.TRUE);
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public boolean isCurrentThreadAnAgentThread() {
        return Thread.currentThread() instanceof AgentThread;
    }
    
    public boolean isAgentThreadId(final Long threadId) {
        return this.agentThreadIds.containsKey(threadId);
    }
    
    public Set<Long> getAgentThreadIds() {
        return Collections.unmodifiableSet((Set<? extends Long>)this.agentThreadIds.keySet());
    }
    
    public void registerAgentThreadId(final long id) {
        this.agentThreadIds.put(id, Boolean.TRUE);
    }
    
    public interface AgentThread
    {
    }
}
