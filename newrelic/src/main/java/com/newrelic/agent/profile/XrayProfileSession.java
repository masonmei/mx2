// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.xray.XRaySession;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.service.ServiceFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import com.newrelic.agent.xray.XRaySessionListener;
import com.newrelic.agent.HarvestListener;

public class XrayProfileSession implements HarvestListener, XRaySessionListener
{
    private static Long PROFILE_ID;
    private static boolean ONLY_RUNNABLE_THREADS;
    private static boolean ONLY_REQUEST_THREADS;
    private static boolean PROFILE_AGENT_CODE;
    private ScheduledFuture<?> profileHandle;
    private final ProfilingTaskController profilingTaskController;
    private final String defaultApplication;
    private final Map<Long, ProfilerParameters> profilerParameters;
    private boolean isSuspended;
    
    public XrayProfileSession() {
        this.profilerParameters = new HashMap<Long, ProfilerParameters>();
        this.defaultApplication = ServiceFactory.getConfigService().getDefaultAgentConfig().getApplicationName();
        this.profilingTaskController = ProfilingTaskControllerFactory.createProfilingTaskController(new XrayProfilingTask());
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
    }
    
    public void afterHarvest(final String appName) {
        if (this.defaultApplication.equals(appName)) {
            if (this.isSuspended && ServiceFactory.getProfilerService().getCurrentSession() == null) {
                this.resume();
            }
            if (this.profileHandle != null) {
                final long oSamplePeriod = this.profilingTaskController.getSamplePeriodInMillis();
                this.profilingTaskController.afterHarvest(appName);
                final long nSamplePeriod = this.profilingTaskController.getSamplePeriodInMillis();
                if (nSamplePeriod != oSamplePeriod) {
                    this.stop();
                    this.start();
                }
            }
        }
    }
    
    public void xraySessionCreated(final XRaySession session) {
        if (session.isRunProfiler()) {
            final ProfilerParameters parameters = new ProfilerParameters(XrayProfileSession.PROFILE_ID, session.getSamplePeriodMilliseconds(), session.getDurationMilliseconds(), XrayProfileSession.ONLY_RUNNABLE_THREADS, XrayProfileSession.ONLY_REQUEST_THREADS, XrayProfileSession.PROFILE_AGENT_CODE, session.getKeyTransactionName(), session.getxRayId(), session.getApplicationName());
            if (!this.profilerParameters.containsKey(session.getxRayId())) {
                this.profilerParameters.put(parameters.getXraySessionId(), parameters);
                this.startProfiling(parameters);
            }
        }
    }
    
    public void xraySessionRemoved(final XRaySession session) {
        if (session.isRunProfiler()) {
            final ProfilerParameters parameters = this.profilerParameters.remove(session.getxRayId());
            if (parameters != null) {
                this.stopProfiling(parameters);
            }
        }
    }
    
    private void startProfiling(final ProfilerParameters parameters) {
        this.profilingTaskController.addProfile(parameters);
        final String msg = MessageFormat.format("Added xray session profiling for {0}", parameters.getKeyTransaction());
        Agent.LOG.info(msg);
        if (!this.isSuspended) {
            this.start();
        }
    }
    
    private void stopProfiling(final ProfilerParameters parameters) {
        this.profilingTaskController.removeProfile(parameters);
        final String msg = MessageFormat.format("Removed xray session profiling for {0}", parameters.getKeyTransaction());
        Agent.LOG.info(msg);
        if (this.profilerParameters.isEmpty()) {
            this.stop();
        }
    }
    
    private void start() {
        if (this.profileHandle == null) {
            final long delay = this.profilingTaskController.getSamplePeriodInMillis();
            final ScheduledExecutorService scheduler = ServiceFactory.getProfilerService().getScheduledExecutorService();
            this.profileHandle = scheduler.scheduleWithFixedDelay(this.profilingTaskController, 0L, delay, TimeUnit.MILLISECONDS);
            Agent.LOG.fine(MessageFormat.format("Started xray profiling task delay = {0}", delay));
        }
    }
    
    private void stop() {
        if (this.profileHandle != null) {
            this.profileHandle.cancel(false);
            this.profileHandle = null;
            final ScheduledExecutorService scheduler = ServiceFactory.getProfilerService().getScheduledExecutorService();
            scheduler.schedule(this.profilingTaskController, 0L, TimeUnit.MILLISECONDS);
            Agent.LOG.fine("Stopped xray profiling task");
        }
    }
    
    public void suspend() {
        if (!this.isSuspended) {
            this.isSuspended = true;
            this.stop();
            Agent.LOG.fine("Suspended xray profiling session");
        }
    }
    
    private void resume() {
        if (this.isSuspended) {
            this.isSuspended = false;
            if (!this.profilerParameters.isEmpty()) {
                this.start();
            }
            Agent.LOG.fine("Resumed xray profiling session");
        }
    }
    
    ProfilingTaskController getProfilingTaskController() {
        return this.profilingTaskController;
    }
    
    static {
        XrayProfileSession.PROFILE_ID = -1L;
        XrayProfileSession.ONLY_RUNNABLE_THREADS = true;
        XrayProfileSession.ONLY_REQUEST_THREADS = false;
        XrayProfileSession.PROFILE_AGENT_CODE = false;
    }
}
