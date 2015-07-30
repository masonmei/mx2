// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.IgnoreSilentlyException;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;

public class ProfileSession
{
    private final ProfileSampler profileSampler;
    private final IProfile profile;
    private final List<IProfile> profiles;
    private final ProfilerService profilerService;
    private final AtomicBoolean done;
    private final AtomicReference<ScheduledFuture<?>> profileHandle;
    private final AtomicReference<ScheduledFuture<?>> timeoutHandle;
    
    public ProfileSession(final ProfilerService profilerService, final ProfilerParameters profilerParameters) {
        this.profileSampler = new ProfileSampler();
        this.profiles = new ArrayList<IProfile>();
        this.done = new AtomicBoolean(false);
        this.profileHandle = new AtomicReference<ScheduledFuture<?>>();
        this.timeoutHandle = new AtomicReference<ScheduledFuture<?>>();
        this.profilerService = profilerService;
        (this.profile = this.createProfile(profilerParameters)).start();
        this.profiles.add(this.profile);
    }
    
    private IProfile createProfile(final ProfilerParameters profilerParameters) {
        return new Profile(profilerParameters);
    }
    
    void start() {
        final long samplePeriodInMillis = this.profile.getProfilerParameters().getSamplePeriodInMillis();
        final long durationInMillis = this.profile.getProfilerParameters().getDurationInMillis();
        if (samplePeriodInMillis == durationInMillis) {
            this.getLogger().info("Starting single sample profiling session");
            this.startSingleSample();
        }
        else {
            this.getLogger().info(MessageFormat.format("Starting profiling session. Duration: {0} ms, sample period: {1} ms", durationInMillis, samplePeriodInMillis));
            this.startMultiSample(samplePeriodInMillis, durationInMillis);
        }
    }
    
    private void startMultiSample(final long samplePeriodInMillis, final long durationInMillis) {
        final ScheduledExecutorService scheduler = this.profilerService.getScheduledExecutorService();
        ScheduledFuture<?> handle = scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    ProfileSession.this.profileSampler.sampleStackTraces(ProfileSession.this.profiles);
                }
                catch (Throwable t) {
                    final String msg = MessageFormat.format("An error occurred collecting a thread sample: {0}", t.toString());
                    if (ProfileSession.this.getLogger().isLoggable(Level.FINER)) {
                        ProfileSession.this.getLogger().log(Level.SEVERE, msg, t);
                    }
                    else {
                        ProfileSession.this.getLogger().severe(msg);
                    }
                }
            }
        }, 0L, samplePeriodInMillis, TimeUnit.MILLISECONDS);
        this.profileHandle.set(handle);
        handle = scheduler.schedule(new Runnable() {
            public void run() {
                ProfileSession.this.profileHandle.get().cancel(false);
                if (!ProfileSession.this.done.getAndSet(true)) {
                    ProfileSession.this.report();
                }
                ProfileSession.this.sessionCompleted();
            }
        }, durationInMillis, TimeUnit.MILLISECONDS);
        this.timeoutHandle.set(handle);
    }
    
    private void startSingleSample() {
        final ScheduledExecutorService scheduler = this.profilerService.getScheduledExecutorService();
        final ScheduledFuture<?> handle = scheduler.schedule(new Runnable() {
            public void run() {
                try {
                    ProfileSession.this.profileSampler.sampleStackTraces(ProfileSession.this.profiles);
                }
                catch (Throwable t) {
                    final String msg = MessageFormat.format("An error occurred collecting a thread sample: {0}", t.toString());
                    if (ProfileSession.this.getLogger().isLoggable(Level.FINER)) {
                        ProfileSession.this.getLogger().log(Level.SEVERE, msg, t);
                    }
                    else {
                        ProfileSession.this.getLogger().severe(msg);
                    }
                }
                if (!ProfileSession.this.done.getAndSet(true)) {
                    ProfileSession.this.report();
                }
                ProfileSession.this.sessionCompleted();
            }
        }, 0L, TimeUnit.MILLISECONDS);
        this.profileHandle.set(handle);
    }
    
    private void report() {
        try {
            this.profile.end();
            this.profile.markInstrumentedMethods();
            this.getLogger().info(MessageFormat.format("Profiler finished with {0} samples", this.profile.getSampleCount()));
        }
        catch (Throwable e) {
            this.getLogger().log(Level.SEVERE, "Error finishing profile - no profiles will be sent", e);
            return;
        }
        try {
            final List<Long> ids = ServiceFactory.getRPMService().sendProfileData(this.profiles);
            this.getLogger().info(MessageFormat.format("Server profile ids: {0}", ids));
        }
        catch (IgnoreSilentlyException e2) {}
        catch (Throwable e) {
            final String msg = MessageFormat.format("Unable to send profile data: {0}", e.toString());
            if (this.getLogger().isLoggable(Level.FINER)) {
                this.getLogger().log(Level.SEVERE, msg, e);
            }
            else {
                this.getLogger().severe(msg);
            }
        }
    }
    
    private void sessionCompleted() {
        this.profilerService.sessionCompleted(this);
    }
    
    void stop(final boolean shouldReport) {
        if (this.done.getAndSet(true)) {
            return;
        }
        this.getLogger().log(Level.INFO, "Stopping profiling session");
        ScheduledFuture<?> handle = this.profileHandle.get();
        if (handle != null) {
            handle.cancel(false);
        }
        handle = this.timeoutHandle.get();
        if (handle != null) {
            handle.cancel(false);
        }
        this.profilerService.getScheduledExecutorService().schedule(new Runnable() {
            public void run() {
                if (shouldReport) {
                    ProfileSession.this.report();
                }
                ProfileSession.this.sessionCompleted();
            }
        }, 0L, TimeUnit.MILLISECONDS);
    }
    
    public boolean isDone() {
        return this.done.get();
    }
    
    public Long getProfileId() {
        return this.profile.getProfileId();
    }
    
    public IProfile getProfile() {
        return this.profile;
    }
    
    private IAgentLogger getLogger() {
        return this.profilerService.getLogger();
    }
}
