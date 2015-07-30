// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.metric.MetricIdRegistry;
import com.newrelic.agent.stats.StatsEngineImpl;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.stats.StatsEngine;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.util.SafeWrappers;
import java.util.concurrent.ScheduledFuture;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import com.newrelic.agent.service.AbstractService;

public class HarvestServiceImpl extends AbstractService implements HarvestService
{
    public static final String HARVEST_THREAD_NAME = "New Relic Harvest Service";
    private static final long INITIAL_DELAY = 30000L;
    private static final long MIN_HARVEST_INTERVAL_IN_NANOSECONDS;
    private static final long REPORTING_PERIOD_IN_MILLISECONDS;
    private final ScheduledExecutorService scheduledExecutor;
    private final List<HarvestListener> harvestListeners;
    private final Map<IRPMService, HarvestTask> harvestTasks;
    
    public HarvestServiceImpl() {
        super(HarvestService.class.getSimpleName());
        this.harvestListeners = new CopyOnWriteArrayList<HarvestListener>();
        this.harvestTasks = new HashMap<IRPMService, HarvestTask>();
        final ThreadFactory threadFactory = new DefaultThreadFactory("New Relic Harvest Service", true);
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        ServiceFactory.getRPMServiceManager().addConnectionListener(new ConnectionListenerImpl());
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() {
    }
    
    public void startHarvest(final IRPMService rpmService) {
        final HarvestTask harvestTask = this.getOrCreateHarvestTask(rpmService);
        harvestTask.start();
    }
    
    private synchronized HarvestTask getOrCreateHarvestTask(final IRPMService rpmService) {
        HarvestTask harvestTask = this.harvestTasks.get(rpmService);
        if (harvestTask == null) {
            harvestTask = new HarvestTask(rpmService);
            this.harvestTasks.put(rpmService, harvestTask);
        }
        return harvestTask;
    }
    
    private synchronized List<HarvestTask> getHarvestTasks() {
        return new ArrayList<HarvestTask>(this.harvestTasks.values());
    }
    
    public void addHarvestListener(final HarvestListener listener) {
        this.harvestListeners.add(listener);
    }
    
    public void removeHarvestListener(final HarvestListener listener) {
        this.harvestListeners.remove(listener);
    }
    
    protected void doStop() {
        final List<HarvestTask> tasks = this.getHarvestTasks();
        for (final HarvestTask task : tasks) {
            task.stop();
        }
        this.scheduledExecutor.shutdown();
    }
    
    private ScheduledFuture<?> scheduleHarvestTask(final HarvestTask harvestTask) {
        return this.scheduledExecutor.scheduleAtFixedRate(SafeWrappers.safeRunnable(harvestTask), this.getInitialDelay(), this.getReportingPeriod(), TimeUnit.MILLISECONDS);
    }
    
    public long getInitialDelay() {
        return 30000L;
    }
    
    public long getReportingPeriod() {
        return HarvestServiceImpl.REPORTING_PERIOD_IN_MILLISECONDS;
    }
    
    public long getMinHarvestInterval() {
        return HarvestServiceImpl.MIN_HARVEST_INTERVAL_IN_NANOSECONDS;
    }
    
    public void harvestNow() {
        final List<HarvestTask> tasks = this.getHarvestTasks();
        for (final HarvestTask task : tasks) {
            task.harvestNow();
        }
    }
    
    private void reportHarvest(final String appName, final StatsEngine statsEngine, final IRPMService rpmService) {
        try {
            rpmService.harvest(statsEngine);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Error reporting harvest data for {0}: {1}", appName, e);
            if (this.getLogger().isLoggable(Level.FINER)) {
                this.getLogger().log(Level.FINER, msg, e);
            }
            else {
                this.getLogger().finer(msg);
            }
        }
    }
    
    private void notifyListenerBeforeHarvest(final String appName, final StatsEngine statsEngine, final HarvestListener listener) {
        try {
            listener.beforeHarvest(appName, statsEngine);
        }
        catch (Throwable e) {
            final String msg = MessageFormat.format("Error harvesting data for {0}: {1}", appName, e);
            if (this.getLogger().isLoggable(Level.FINER)) {
                this.getLogger().log(Level.FINER, msg, e);
            }
            else {
                this.getLogger().finer(msg);
            }
        }
    }
    
    private void notifyListenerAfterHarvest(final String appName, final HarvestListener listener) {
        try {
            listener.afterHarvest(appName);
        }
        catch (Throwable e) {
            final String msg = MessageFormat.format("Error harvesting data for {0}: {1}", appName, e);
            if (this.getLogger().isLoggable(Level.FINER)) {
                this.getLogger().log(Level.FINER, msg, e);
            }
            else {
                this.getLogger().finer(msg);
            }
        }
    }
    
    static {
        MIN_HARVEST_INTERVAL_IN_NANOSECONDS = TimeUnit.NANOSECONDS.convert(55L, TimeUnit.SECONDS);
        REPORTING_PERIOD_IN_MILLISECONDS = TimeUnit.MILLISECONDS.convert(60L, TimeUnit.SECONDS);
    }
    
    private final class HarvestTask implements Runnable
    {
        private final IRPMService rpmService;
        private ScheduledFuture<?> task;
        private final Lock harvestLock;
        private StatsEngine lastStatsEngine;
        private long lastHarvestStartTime;
        
        private HarvestTask(final IRPMService rpmService) {
            this.harvestLock = new ReentrantLock();
            this.lastStatsEngine = new StatsEngineImpl();
            this.rpmService = rpmService;
        }
        
        public void run() {
            try {
                if (this.shouldHarvest()) {
                    this.harvest();
                }
            }
            catch (Throwable t) {
                final String msg = MessageFormat.format("Unexpected exception during harvest: {0}", t);
                if (HarvestServiceImpl.this.getLogger().isLoggable(Level.FINER)) {
                    HarvestServiceImpl.this.getLogger().log(Level.WARNING, msg, t);
                }
                else {
                    HarvestServiceImpl.this.getLogger().warning(msg);
                }
            }
        }
        
        private boolean shouldHarvest() {
            return System.nanoTime() - this.lastHarvestStartTime >= HarvestServiceImpl.this.getMinHarvestInterval();
        }
        
        private synchronized void start() {
            if (!this.isRunning()) {
                this.stop();
                final String msg = MessageFormat.format("Scheduling harvest task for {0}", this.rpmService.getApplicationName());
                HarvestServiceImpl.this.getLogger().log(Level.FINE, msg);
                this.task = HarvestServiceImpl.this.scheduleHarvestTask(this);
            }
        }
        
        private synchronized void stop() {
            if (this.task != null) {
                HarvestServiceImpl.this.getLogger().fine(MessageFormat.format("Cancelling harvest task for {0}", this.rpmService.getApplicationName()));
                this.task.cancel(false);
            }
        }
        
        private boolean isRunning() {
            return this.task != null && (!this.task.isCancelled() || this.task.isDone());
        }
        
        private void harvestNow() {
            if (this.rpmService.isConnected()) {
                final String msg = MessageFormat.format("Sending metrics for {0} immediately", this.rpmService.getApplicationName());
                HarvestServiceImpl.this.getLogger().info(msg);
                this.harvest();
            }
        }
        
        private void harvest() {
            this.harvestLock.lock();
            try {
                this.doHarvest();
            }
            catch (ServerCommandException e2) {}
            catch (IgnoreSilentlyException e3) {}
            catch (Throwable e) {
                HarvestServiceImpl.this.getLogger().log(Level.INFO, "Error sending metric data for {0}: {1}", new Object[] { this.rpmService.getApplicationName(), e.toString() });
            }
            finally {
                this.harvestLock.unlock();
            }
        }
        
        private void doHarvest() throws Exception {
            this.lastHarvestStartTime = System.nanoTime();
            final String appName = this.rpmService.getApplicationName();
            if (HarvestServiceImpl.this.getLogger().isLoggable(Level.FINE)) {
                final String msg = MessageFormat.format("Starting harvest for {0}", appName);
                HarvestServiceImpl.this.getLogger().fine(msg);
            }
            final StatsEngine harvestStatsEngine = ServiceFactory.getStatsService().getStatsEngineForHarvest(appName);
            harvestStatsEngine.mergeStats(this.lastStatsEngine);
            try {
                for (final HarvestListener listener : HarvestServiceImpl.this.harvestListeners) {
                    HarvestServiceImpl.this.notifyListenerBeforeHarvest(appName, harvestStatsEngine, listener);
                }
                HarvestServiceImpl.this.reportHarvest(appName, harvestStatsEngine, this.rpmService);
                for (final HarvestListener listener : HarvestServiceImpl.this.harvestListeners) {
                    HarvestServiceImpl.this.notifyListenerAfterHarvest(appName, listener);
                }
                if (harvestStatsEngine.getSize() > MetricIdRegistry.METRIC_LIMIT) {
                    harvestStatsEngine.clear();
                }
                this.lastStatsEngine = harvestStatsEngine;
                final long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - this.lastHarvestStartTime, TimeUnit.NANOSECONDS);
                harvestStatsEngine.getResponseTimeStats("Supportability/Harvest").recordResponseTime(duration, TimeUnit.MILLISECONDS);
                if (HarvestServiceImpl.this.getLogger().isLoggable(Level.FINE)) {
                    final String msg2 = MessageFormat.format("Harvest for {0} took {1} milliseconds", appName, duration);
                    HarvestServiceImpl.this.getLogger().fine(msg2);
                }
            }
            finally {
                if (harvestStatsEngine.getSize() > MetricIdRegistry.METRIC_LIMIT) {
                    harvestStatsEngine.clear();
                }
                this.lastStatsEngine = harvestStatsEngine;
                final long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - this.lastHarvestStartTime, TimeUnit.NANOSECONDS);
                harvestStatsEngine.getResponseTimeStats("Supportability/Harvest").recordResponseTime(duration, TimeUnit.MILLISECONDS);
                if (HarvestServiceImpl.this.getLogger().isLoggable(Level.FINE)) {
                    final String msg2 = MessageFormat.format("Harvest for {0} took {1} milliseconds", appName, duration);
                    HarvestServiceImpl.this.getLogger().fine(msg2);
                }
            }
        }
    }
    
    private class ConnectionListenerImpl implements ConnectionListener
    {
        public void connected(final IRPMService rpmService, final Map<String, Object> connectionInfo) {
            HarvestServiceImpl.this.startHarvest(rpmService);
        }
        
        public void disconnected(final IRPMService rpmService) {
        }
    }
}
