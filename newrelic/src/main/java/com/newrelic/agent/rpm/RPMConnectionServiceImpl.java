// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.rpm;

import com.newrelic.agent.environment.Environment;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.ConfigService;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.util.SafeWrappers;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import com.newrelic.agent.IRPMService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import java.util.concurrent.ScheduledExecutorService;
import com.newrelic.agent.service.AbstractService;

public class RPMConnectionServiceImpl extends AbstractService implements RPMConnectionService
{
    public static final String RPM_CONNECTION_THREAD_NAME = "New Relic RPM Connection Service";
    public static final long INITIAL_APP_SERVER_PORT_DELAY = 5L;
    public static final long SUBSEQUENT_APP_SERVER_PORT_DELAY = 5L;
    public static final long APP_SERVER_PORT_TIMEOUT = 120L;
    public static final long CONNECT_ATTEMPT_INTERVAL = 60L;
    private final ScheduledExecutorService scheduledExecutor;
    
    public RPMConnectionServiceImpl() {
        super(RPMConnectionService.class.getSimpleName());
        final ThreadFactory threadFactory = new DefaultThreadFactory("New Relic RPM Connection Service", true);
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }
    
    protected void doStart() {
    }
    
    protected void doStop() {
        this.scheduledExecutor.shutdown();
    }
    
    public void connect(final IRPMService rpmService) {
        final RPMConnectionTask connectionTask = new RPMConnectionTask(rpmService);
        connectionTask.start();
    }
    
    public void connectImmediate(final IRPMService rpmService) {
        final RPMConnectionTask connectionTask = new RPMConnectionTask(rpmService);
        connectionTask.startImmediate();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public long getInitialAppServerPortDelay() {
        return 5L;
    }
    
    public long getAppServerPortTimeout() {
        return 120L;
    }
    
    private final class RPMConnectionTask implements Runnable
    {
        private final IRPMService rpmService;
        private final AtomicReference<ScheduledFuture<?>> appServerPortTask;
        private final AtomicReference<ScheduledFuture<?>> appServerPortTimeoutTask;
        private final AtomicReference<ScheduledFuture<?>> connectTask;
        private final AtomicBoolean connectTaskStarted;
        
        private RPMConnectionTask(final IRPMService rpmService) {
            this.appServerPortTask = new AtomicReference<ScheduledFuture<?>>();
            this.appServerPortTimeoutTask = new AtomicReference<ScheduledFuture<?>>();
            this.connectTask = new AtomicReference<ScheduledFuture<?>>();
            this.connectTaskStarted = new AtomicBoolean();
            this.rpmService = rpmService;
        }
        
        public void run() {
        }
        
        private void start() {
            if (!this.rpmService.isMainApp()) {
                this.startImmediate();
            }
            else if (this.isSyncStartup()) {
                RPMConnectionServiceImpl.this.getLogger().log(Level.FINER, "Not waiting for application server port");
                this.startSync();
            }
            else {
                RPMConnectionServiceImpl.this.getLogger().log(Level.FINER, "Waiting for application server port");
                this.appServerPortTask.set(this.scheduleAppServerPortTask());
                this.appServerPortTimeoutTask.set(this.scheduleAppServerPortTimeoutTask());
            }
        }
        
        private void startSync() {
            if (this.isConnected() || this.attemptConnection()) {
                return;
            }
            this.startImmediate();
        }
        
        private void startImmediate() {
            this.connectTask.set(this.scheduleConnectTask());
        }
        
        private void stop() {
            RPMConnectionServiceImpl.this.getLogger().log(Level.FINER, "Stopping New Relic connection task for {0}", new Object[] { this.rpmService.getApplicationName() });
            ScheduledFuture<?> handle = this.appServerPortTask.get();
            if (handle != null) {
                handle.cancel(false);
            }
            handle = this.connectTask.get();
            if (handle != null) {
                handle.cancel(false);
            }
            handle = this.appServerPortTimeoutTask.get();
            if (handle != null) {
                handle.cancel(false);
            }
        }
        
        private ScheduledFuture<?> scheduleAppServerPortTask() {
            return RPMConnectionServiceImpl.this.scheduledExecutor.scheduleWithFixedDelay(SafeWrappers.safeRunnable(new Runnable() {
                public void run() {
                    if (RPMConnectionTask.this.isConnected()) {
                        RPMConnectionTask.this.stop();
                        return;
                    }
                    if (RPMConnectionTask.this.hasAppServerPort() && !RPMConnectionTask.this.connectTaskStarted()) {
                        RPMConnectionTask.this.stop();
                        RPMConnectionServiceImpl.this.getLogger().log(Level.FINER, "Discovered application server port");
                        RPMConnectionTask.this.connectTask.set(RPMConnectionTask.this.scheduleConnectTask());
                    }
                }
            }), RPMConnectionServiceImpl.this.getInitialAppServerPortDelay(), 5L, TimeUnit.SECONDS);
        }
        
        private ScheduledFuture<?> scheduleAppServerPortTimeoutTask() {
            return RPMConnectionServiceImpl.this.scheduledExecutor.schedule(SafeWrappers.safeRunnable(new Runnable() {
                public void run() {
                    if (!RPMConnectionTask.this.connectTaskStarted()) {
                        RPMConnectionTask.this.stop();
                        if (!RPMConnectionTask.this.isConnected()) {
                            if (!RPMConnectionTask.this.hasAppServerPort()) {
                                RPMConnectionServiceImpl.this.getLogger().log(Level.FINER, "Gave up waiting for application server port");
                            }
                            RPMConnectionTask.this.connectTask.set(RPMConnectionTask.this.scheduleConnectTask());
                        }
                    }
                }
            }), RPMConnectionServiceImpl.this.getAppServerPortTimeout(), TimeUnit.SECONDS);
        }
        
        private ScheduledFuture<?> scheduleConnectTask() {
            return RPMConnectionServiceImpl.this.scheduledExecutor.scheduleWithFixedDelay(SafeWrappers.safeRunnable(new Runnable() {
                public void run() {
                    if (RPMConnectionTask.this.shouldAttemptConnection() && RPMConnectionTask.this.attemptConnection()) {
                        RPMConnectionTask.this.stop();
                    }
                }
            }), 0L, 60L, TimeUnit.SECONDS);
        }
        
        private boolean isMainAppConnected() {
            return ServiceFactory.getRPMService().isConnected();
        }
        
        private boolean isConnected() {
            return this.rpmService.isConnected();
        }
        
        private boolean connectTaskStarted() {
            return this.connectTaskStarted.getAndSet(true);
        }
        
        private boolean hasAppServerPort() {
            return this.getEnvironment().getAgentIdentity().getServerPort() != null;
        }
        
        private boolean isSyncStartup() {
            final ConfigService configService = ServiceFactory.getConfigService();
            final AgentConfig config = configService.getAgentConfig(this.rpmService.getApplicationName());
            return config.isSyncStartup();
        }
        
        private boolean shouldAttemptConnection() {
            return (this.rpmService.isMainApp() || this.isMainAppConnected()) && !this.isConnected();
        }
        
        private boolean attemptConnection() {
            try {
                this.rpmService.launch();
                return true;
            }
            catch (Throwable e) {
                RPMConnectionServiceImpl.this.getLogger().log(Level.INFO, "Failed to connect to {0} for {1}: {2}", new Object[] { this.rpmService.getHostString(), this.rpmService.getApplicationName(), e.toString() });
                RPMConnectionServiceImpl.this.getLogger().log(Level.FINEST, e, e.toString(), new Object[0]);
                return false;
            }
        }
        
        private Environment getEnvironment() {
            return ServiceFactory.getEnvironmentService().getEnvironment();
        }
    }
}
