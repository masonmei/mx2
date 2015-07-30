// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import com.newrelic.agent.IgnoreSilentlyException;
import java.util.Arrays;
import com.newrelic.agent.service.ServiceFactory;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.stats.StatsEngine;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;

public class XrayProfilingTask implements ProfilingTask
{
    private final List<ProfilerParameters> profilesToAdd;
    private final List<ProfilerParameters> profilesToRemove;
    private final List<IProfile> profiles;
    private final AtomicBoolean sendProfiles;
    private final ProfileSampler profileSampler;
    
    public XrayProfilingTask() {
        this.profilesToAdd = new CopyOnWriteArrayList<ProfilerParameters>();
        this.profilesToRemove = new CopyOnWriteArrayList<ProfilerParameters>();
        this.profiles = new ArrayList<IProfile>();
        this.sendProfiles = new AtomicBoolean(false);
        this.profileSampler = new ProfileSampler();
    }
    
    public void addProfile(final ProfilerParameters parameters) {
        this.profilesToAdd.add(parameters);
    }
    
    public void removeProfile(final ProfilerParameters parameters) {
        this.profilesToRemove.add(parameters);
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
    }
    
    public void afterHarvest(final String appName) {
        this.sendProfiles.set(true);
    }
    
    public void run() {
        try {
            this.sampleStackTraces();
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Error sampling stack traces: {0}", t);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg, t);
            }
            else {
                Agent.LOG.finer(msg);
            }
        }
    }
    
    private void sampleStackTraces() {
        this.removeProfiles();
        if (this.sendProfiles.getAndSet(false)) {
            this.sendProfiles();
        }
        this.addProfiles();
        this.profileSampler.sampleStackTraces(this.profiles);
    }
    
    private void removeProfiles() {
        for (final ProfilerParameters parameters : this.profilesToRemove) {
            final IProfile profile = this.getProfile(parameters);
            if (profile != null) {
                this.profiles.remove(profile);
                Agent.LOG.info(MessageFormat.format("Stopped xray session profiling: {0}", parameters.getKeyTransaction()));
            }
            this.profilesToRemove.remove(parameters);
        }
    }
    
    private void addProfiles() {
        for (final ProfilerParameters parameters : this.profilesToAdd) {
            IProfile profile = this.getProfile(parameters);
            if (profile == null) {
                profile = this.createProfile(parameters);
                profile.start();
                this.profiles.add(profile);
                Agent.LOG.info(MessageFormat.format("Started xray session profiling: {0}", parameters.getKeyTransaction()));
            }
            this.profilesToAdd.remove(parameters);
        }
    }
    
    List<IProfile> getProfiles() {
        return new CopyOnWriteArrayList<IProfile>(this.profiles);
    }
    
    private IProfile getProfile(final ProfilerParameters parameters) {
        for (final IProfile profile : this.profiles) {
            if (profile.getProfilerParameters().equals(parameters)) {
                return profile;
            }
        }
        return null;
    }
    
    private IProfile createProfile(final ProfilerParameters parameters) {
        return new KeyTransactionProfile(parameters);
    }
    
    private void sendProfiles() {
        final ListIterator<IProfile> it = this.profiles.listIterator();
        while (it.hasNext()) {
            final IProfile profile = it.next();
            final int requestCallSiteCount = profile.getProfileTree(ThreadType.BasicThreadType.REQUEST).getCallSiteCount();
            final int backgroundCallSiteCount = profile.getProfileTree(ThreadType.BasicThreadType.BACKGROUND).getCallSiteCount();
            if (requestCallSiteCount > 0 || backgroundCallSiteCount > 0) {
                it.remove();
                profile.end();
                final IProfile nProfile = this.createProfile(profile.getProfilerParameters());
                nProfile.start();
                it.add(nProfile);
                this.sendProfile(profile);
            }
        }
    }
    
    private void sendProfile(final IProfile profile) {
        try {
            if (Agent.LOG.isLoggable(Level.FINE)) {
                final String msg = MessageFormat.format("Sending Xray profile: {0}", profile.getProfilerParameters().getXraySessionId());
                Agent.LOG.fine(msg);
            }
            final String appName = profile.getProfilerParameters().getAppName();
            final List<Long> ids = ServiceFactory.getRPMService(appName).sendProfileData(Arrays.asList(profile));
            if (Agent.LOG.isLoggable(Level.FINE)) {
                Agent.LOG.fine(MessageFormat.format("Xray profile id: {0}", ids));
            }
        }
        catch (IgnoreSilentlyException e2) {}
        catch (Exception e) {
            final String msg2 = MessageFormat.format("Unable to send profile data: {0}", e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg2, e);
            }
            else {
                Agent.LOG.fine(msg2);
            }
        }
    }
}
