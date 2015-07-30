// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import java.io.IOException;
import com.newrelic.agent.util.SafeWrappers;
import java.io.Closeable;
import com.newrelic.agent.stats.StatsWork;
import com.newrelic.agent.stats.StatsService;
import com.newrelic.agent.stats.MergeStatsEngine;
import java.util.List;
import com.newrelic.agent.IRPMService;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.config.AgentConfig;
import java.util.concurrent.ThreadFactory;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import com.newrelic.agent.stats.StatsEngineImpl;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.IAgent;
import com.newrelic.agent.stats.StatsEngine;
import java.util.concurrent.ScheduledFuture;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import com.newrelic.agent.service.AbstractService;

public class SamplerServiceImpl extends AbstractService implements SamplerService
{
    private static final String SAMPLER_THREAD_NAME = "New Relic Sampler Service";
    private static final long INITIAL_DELAY_IN_MILLISECONDS = 1000L;
    private static final long DELAY_IN_MILLISECONDS = 5000L;
    private final ScheduledExecutorService scheduledExecutor;
    private final Set<ScheduledFuture<?>> tasks;
    private final StatsEngine statsEngine;
    private final IAgent agent;
    private final String defaultAppName;
    private final boolean isAutoAppNamingEnabled;
    
    public SamplerServiceImpl() {
        super(SamplerService.class.getSimpleName());
        this.tasks = Sets.newSetFromMap((Map<ScheduledFuture<?>, Boolean>)Maps.newConcurrentMap());
        this.statsEngine = new StatsEngineImpl();
        final ThreadFactory threadFactory = new DefaultThreadFactory("New Relic Sampler Service", true);
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        this.agent = ServiceFactory.getAgent();
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.isAutoAppNamingEnabled = config.isAutoAppNamingEnabled();
        this.defaultAppName = config.getApplicationName();
    }
    
    protected void doStart() {
        final MemorySampler memorySampler = new MemorySampler();
        memorySampler.start();
        this.addMetricSampler(memorySampler, 1000L, 5000L, TimeUnit.MILLISECONDS);
        final ThreadSampler threadSampler = new ThreadSampler();
        this.addMetricSampler(threadSampler, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }
    
    protected void doStop() {
        synchronized (this.tasks) {
            for (final ScheduledFuture<?> task : this.tasks) {
                task.cancel(false);
            }
            this.tasks.clear();
        }
        this.scheduledExecutor.shutdown();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    private void addMetricSampler(final MetricSampler sampler, final long initialDelay, final long delay, final TimeUnit unit) {
        final Runnable runnable = new Runnable() {
            public void run() {
                try {
                    SamplerServiceImpl.this.runSampler(sampler);
                    SamplerServiceImpl.this.statsEngine.clear();
                }
                catch (Throwable t) {
                    final String msg = MessageFormat.format("Unable to sample {0}: {1}", this.getClass().getName(), t);
                    if (SamplerServiceImpl.this.getLogger().isLoggable(Level.FINER)) {
                        SamplerServiceImpl.this.getLogger().log(Level.WARNING, msg, t);
                    }
                    else {
                        SamplerServiceImpl.this.getLogger().warning(msg);
                    }
                    SamplerServiceImpl.this.statsEngine.clear();
                }
                finally {
                    SamplerServiceImpl.this.statsEngine.clear();
                }
            }
        };
        this.addSampler(runnable, delay, unit);
    }
    
    private void runSampler(final MetricSampler sampler) {
        if (!this.agent.isEnabled()) {
            return;
        }
        sampler.sample(this.statsEngine);
        if (!this.isAutoAppNamingEnabled) {
            this.mergeStatsEngine(this.defaultAppName);
            return;
        }
        final List<IRPMService> rpmServices = ServiceFactory.getRPMServiceManager().getRPMServices();
        for (final IRPMService rpmService : rpmServices) {
            final String appName = rpmService.getApplicationName();
            this.mergeStatsEngine(appName);
        }
    }
    
    private void mergeStatsEngine(final String appName) {
        final StatsService statsService = ServiceFactory.getStatsService();
        final StatsWork work = new MergeStatsEngine(appName, this.statsEngine);
        statsService.doStatsWork(work);
    }
    
    public Closeable addSampler(final Runnable sampler, final long period, final TimeUnit timeUnit) {
        if (this.scheduledExecutor.isShutdown()) {
            return null;
        }
        final ScheduledFuture<?> task = this.scheduledExecutor.scheduleWithFixedDelay(SafeWrappers.safeRunnable(sampler), period, period, timeUnit);
        this.tasks.add(task);
        return new Closeable() {
            public void close() throws IOException {
                SamplerServiceImpl.this.tasks.remove(task);
                task.cancel(false);
            }
        };
    }
}
