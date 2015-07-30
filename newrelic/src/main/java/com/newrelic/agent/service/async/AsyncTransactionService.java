// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.async;

import com.newrelic.agent.deps.com.google.common.cache.RemovalCause;
import com.newrelic.agent.deps.com.google.common.cache.RemovalNotification;
import com.newrelic.agent.deps.com.google.common.collect.Queues;
import com.newrelic.agent.stats.StatsEngine;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.com.google.common.cache.Cache;
import com.newrelic.agent.deps.com.google.common.cache.RemovalListener;
import com.newrelic.agent.Transaction;
import java.util.Map;
import java.util.Queue;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class AsyncTransactionService extends AbstractService implements HarvestListener
{
    private static final Queue<Map.Entry<Object, Transaction>> TIMED_OUT;
    private static final Map.Entry<Object, Transaction> NO_OP_MARKER;
    private static final RemovalListener<Object, Transaction> removalListener;
    private static final Cache<Object, Transaction> PENDING_ACTIVITIES;
    
    public AsyncTransactionService() {
        super(AsyncTransactionService.class.getSimpleName());
    }
    
    private static final Cache<Object, Transaction> makeCache(final RemovalListener<Object, Transaction> removalListener) {
        final long timeoutSec = (int)ServiceFactory.getConfigService().getDefaultAgentConfig().getValue("async_timeout", (Object)180);
        return CacheBuilder.newBuilder().weakKeys().expireAfterWrite(timeoutSec, TimeUnit.SECONDS).removalListener((RemovalListener<? super Object, ? super Object>)removalListener).build();
    }
    
    public void cleanUpPendingTransactions() {
        AsyncTransactionService.PENDING_ACTIVITIES.cleanUp();
        Agent.LOG.log(Level.FINER, "Cleaning up the pending activities cache.");
    }
    
    public boolean putIfAbsent(final Object key, final Transaction tx) {
        boolean result = false;
        synchronized (AsyncTransactionService.PENDING_ACTIVITIES) {
            if (AsyncTransactionService.PENDING_ACTIVITIES.getIfPresent(key) == null) {
                AsyncTransactionService.PENDING_ACTIVITIES.put(key, tx);
                result = true;
            }
        }
        return result;
    }
    
    public Transaction extractIfPresent(final Object key) {
        Transaction result = null;
        synchronized (AsyncTransactionService.PENDING_ACTIVITIES) {
            result = AsyncTransactionService.PENDING_ACTIVITIES.getIfPresent(key);
            if (result != null) {
                AsyncTransactionService.PENDING_ACTIVITIES.invalidate(key);
            }
        }
        return result;
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        this.cleanUpPendingTransactions();
        Map.Entry<Object, Transaction> notification = AsyncTransactionService.TIMED_OUT.poll();
        if (notification != null) {
            Agent.LOG.log(Level.FINER, "Pulling async keys from timeout queue.");
            AsyncTransactionService.TIMED_OUT.add(AsyncTransactionService.NO_OP_MARKER);
            while (notification != AsyncTransactionService.NO_OP_MARKER) {
                notification.getValue().timeoutAsyncActivity(notification.getKey());
                Agent.LOG.log(Level.FINER, "Timed out key {0} in transaction {1}", new Object[] { notification.getKey(), notification.getValue() });
                notification = AsyncTransactionService.TIMED_OUT.poll();
            }
        }
    }
    
    protected int queueSizeForTesting() {
        return AsyncTransactionService.TIMED_OUT.size();
    }
    
    public void afterHarvest(final String appName) {
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() throws Exception {
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getHarvestService().removeHarvestListener(this);
    }
    
    static {
        TIMED_OUT = Queues.newConcurrentLinkedQueue();
        NO_OP_MARKER = new Map.Entry<Object, Transaction>() {
            public Object getKey() {
                return null;
            }
            
            public Transaction getValue() {
                return null;
            }
            
            public Transaction setValue(final Transaction value) {
                return null;
            }
        };
        removalListener = new RemovalListener<Object, Transaction>() {
            public void onRemoval(final RemovalNotification<Object, Transaction> notification) {
                final RemovalCause cause = notification.getCause();
                if (cause == RemovalCause.EXPLICIT) {
                    Agent.LOG.log(Level.FINEST, "{2}: Key {0} with transaction {1} removed from cache.", new Object[] { notification.getKey(), notification.getValue(), cause });
                }
                else {
                    Agent.LOG.log(Level.FINE, "{2}: The registered async activity with async context {0} has timed out for transaction {1} and been removed from the cache.", new Object[] { notification.getKey(), notification.getValue(), cause });
                    AsyncTransactionService.TIMED_OUT.add(notification);
                }
            }
        };
        PENDING_ACTIVITIES = makeCache(AsyncTransactionService.removalListener);
    }
}
