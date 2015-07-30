// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import com.newrelic.agent.ThreadService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Agent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public class DefaultThreadFactory implements ThreadFactory
{
    private final String name;
    private final boolean daemon;
    private final AtomicInteger threadNumber;
    
    public DefaultThreadFactory(final String name, final boolean daemon) {
        this.threadNumber = new AtomicInteger(1);
        this.name = name;
        this.daemon = daemon;
    }
    
    public Thread newThread(final Runnable r) {
        final int num = this.threadNumber.getAndIncrement();
        final String threadName = (num == 1) ? this.name : (this.name + " " + num);
        final Thread t = new AgentThreadImpl(r, threadName);
        Agent.LOG.fine("Created agent thread: " + t.getName());
        ServiceFactory.getThreadService().registerAgentThreadId(t.getId());
        if (this.daemon) {
            t.setDaemon(true);
        }
        return t;
    }
    
    private static class AgentThreadImpl extends Thread implements ThreadService.AgentThread
    {
        public AgentThreadImpl(final Runnable r, final String threadName) {
            super(r, threadName);
        }
    }
}
