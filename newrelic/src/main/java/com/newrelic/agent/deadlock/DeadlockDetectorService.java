// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deadlock;

import java.util.concurrent.TimeUnit;
import com.newrelic.agent.util.SafeWrappers;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.concurrent.ThreadFactory;
import com.newrelic.agent.config.AgentConfig;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import com.newrelic.agent.service.AbstractService;

public class DeadlockDetectorService extends AbstractService
{
    private static final String DEADLOCK_DETECTOR_THREAD_NAME = "New Relic Deadlock Detector";
    private static final long INITIAL_DELAY_IN_SECONDS = 300L;
    private static final long SUBSEQUENT_DELAY_IN_SECONDS = 300L;
    private final boolean isEnabled;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile ScheduledFuture<?> deadlockTask;
    
    public DeadlockDetectorService() {
        super(DeadlockDetectorService.class.getSimpleName());
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.isEnabled = (Boolean)config.getValue("deadlock_detector.enabled", (Object)true);
        final ThreadFactory threadFactory = this.isEnabled ? new DefaultThreadFactory("New Relic Deadlock Detector", true) : null;
        this.scheduledExecutor = (this.isEnabled ? Executors.newSingleThreadScheduledExecutor(threadFactory) : null);
    }
    
    protected void doStart() {
        if (!this.isEnabled) {
            return;
        }
        final DeadLockDetector deadlockDetector = this.getDeadlockDetector();
        try {
            deadlockDetector.detectDeadlockedThreads();
        }
        catch (Throwable t) {
            this.logger.log(Level.FINE, t, "Failed to detect deadlocked threads: {0}.  The Deadlock detector is disabled.", new Object[] { t.toString() });
            this.logger.log(Level.FINEST, t, t.toString(), new Object[0]);
            return;
        }
        final Runnable runnable = new Runnable() {
            public void run() {
                try {
                    deadlockDetector.detectDeadlockedThreads();
                }
                catch (Throwable t) {
                    final String msg = MessageFormat.format("Failed to detect deadlocked threads: {0}", t.toString());
                    if (DeadlockDetectorService.this.getLogger().isLoggable(Level.FINER)) {
                        DeadlockDetectorService.this.getLogger().log(Level.WARNING, msg, t);
                    }
                    else {
                        DeadlockDetectorService.this.getLogger().warning(msg);
                    }
                }
            }
        };
        this.deadlockTask = this.scheduledExecutor.scheduleWithFixedDelay(SafeWrappers.safeRunnable(runnable), 300L, 300L, TimeUnit.SECONDS);
    }
    
    protected void doStop() {
        if (!this.isEnabled) {
            return;
        }
        if (this.deadlockTask != null) {
            this.deadlockTask.cancel(false);
        }
        this.scheduledExecutor.shutdown();
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    private DeadLockDetector getDeadlockDetector() {
        return new DeadLockDetector();
    }
}
