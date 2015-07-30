// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.commands.Command;
import com.newrelic.agent.xray.XRaySessionListener;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.util.SafeWrappers;
import java.text.MessageFormat;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import java.util.concurrent.ScheduledExecutorService;
import com.newrelic.agent.service.AbstractService;

public class ProfilerService extends AbstractService implements ProfilerControl
{
    private static final String PROFILER_THREAD_NAME = "New Relic Profiler Service";
    private ProfileSession currentSession;
    private final XrayProfileSession xrayProfileSession;
    private final ScheduledExecutorService scheduledExecutor;
    
    public ProfilerService() {
        super(ProfilerService.class.getSimpleName());
        final ThreadFactory threadFactory = new DefaultThreadFactory("New Relic Profiler Service", true);
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        this.xrayProfileSession = new XrayProfileSession();
    }
    
    public boolean isEnabled() {
        return ServiceFactory.getConfigService().getDefaultAgentConfig().getThreadProfilerConfig().isEnabled();
    }
    
    public synchronized void startProfiler(final ProfilerParameters parameters) {
        final long samplePeriodInMillis = parameters.getSamplePeriodInMillis();
        final long durationInMillis = parameters.getDurationInMillis();
        final boolean enabled = ServiceFactory.getConfigService().getDefaultAgentConfig().getThreadProfilerConfig().isEnabled();
        if (!enabled || samplePeriodInMillis <= 0L || durationInMillis <= 0L || samplePeriodInMillis > durationInMillis) {
            this.getLogger().info(MessageFormat.format("Ignoring the start profiler command: enabled={0}, samplePeriodInMillis={1}, durationInMillis={2}", enabled, samplePeriodInMillis, durationInMillis));
            return;
        }
        final ProfileSession oldSession = this.currentSession;
        if (oldSession != null && !oldSession.isDone()) {
            this.getLogger().info(MessageFormat.format("Ignoring the start profiler command because a session is currently active. {0}", oldSession.getProfileId()));
            return;
        }
        this.xrayProfileSession.suspend();
        final ProfileSession newSession = this.createProfileSession(parameters);
        newSession.start();
        this.currentSession = newSession;
    }
    
    public synchronized int stopProfiler(final Long profileId, final boolean shouldReport) {
        final ProfileSession session = this.currentSession;
        if (session != null && profileId.equals(session.getProfileId())) {
            session.stop(shouldReport);
            return 0;
        }
        return -1;
    }
    
    synchronized void sessionCompleted(final ProfileSession session) {
        if (this.currentSession != session) {
            return;
        }
        this.currentSession = null;
    }
    
    protected ProfileSession createProfileSession(final ProfilerParameters parameters) {
        return new ProfileSession(this, parameters);
    }
    
    protected ScheduledExecutorService getScheduledExecutorService() {
        return SafeWrappers.safeExecutor(this.scheduledExecutor);
    }
    
    protected void doStart() {
        this.addCommands();
        if (this.isEnabled()) {
            ServiceFactory.getHarvestService().addHarvestListener(this.xrayProfileSession);
            ServiceFactory.getXRaySessionService().addListener(this.xrayProfileSession);
        }
    }
    
    protected synchronized ProfileSession getCurrentSession() {
        return this.currentSession;
    }
    
    private void addCommands() {
        ServiceFactory.getCommandParser().addCommands(new StartProfilerCommand(this));
        ServiceFactory.getCommandParser().addCommands(new StopProfilerCommand(this));
    }
    
    protected void doStop() {
        if (this.isEnabled()) {
            ServiceFactory.getHarvestService().removeHarvestListener(this.xrayProfileSession);
            ServiceFactory.getXRaySessionService().removeListener(this.xrayProfileSession);
        }
        final ProfileSession session = this.getCurrentSession();
        if (session != null) {
            session.stop(false);
        }
        if (this.scheduledExecutor != null) {
            this.scheduledExecutor.shutdown();
            try {
                if (!this.scheduledExecutor.awaitTermination(30L, TimeUnit.SECONDS)) {
                    this.getLogger().log(Level.FINE, "Profiler Service executor service did not terminate");
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
