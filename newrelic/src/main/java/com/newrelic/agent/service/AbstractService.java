// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service;

import java.util.concurrent.atomic.AtomicReference;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.logging.IAgentLogger;

public abstract class AbstractService implements Service
{
    protected final IAgentLogger logger;
    private final String name;
    private final State state;
    
    protected AbstractService(final String name) {
        this.state = new State();
        this.name = name;
        this.logger = Agent.LOG.getChildLogger(this.getClass());
        ServiceTiming.addServiceInitialization(name);
    }
    
    public IAgentLogger getLogger() {
        return this.logger;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public boolean isStartedOrStarting() {
        return this.state.isStartedOrStarting();
    }
    
    public boolean isStoppedOrStopping() {
        return this.state.isStoppedOrStopping();
    }
    
    public boolean isStarted() {
        return this.state.isStarted();
    }
    
    public boolean isStopped() {
        return this.state.isStopped();
    }
    
    public final void start() throws Exception {
        if (this.state.beginStart()) {
            this.getLogger().fine(MessageFormat.format("Starting service {0}", this.name));
            ServiceTiming.addServiceStart(this.name);
            this.doStart();
            this.state.endStart();
        }
    }
    
    protected abstract void doStart() throws Exception;
    
    public final void stop() throws Exception {
        if (this.state.beginStop()) {
            this.getLogger().fine(MessageFormat.format("Stopping service {0}", this.name));
            this.doStop();
            this.state.endStop();
        }
    }
    
    protected abstract void doStop() throws Exception;
    
    private static final class State
    {
        private AtomicReference<ServiceState> serviceState;
        
        private State() {
            this.serviceState = new AtomicReference<ServiceState>(ServiceState.STOPPED);
        }
        
        private boolean beginStart() {
            return this.serviceState.compareAndSet(ServiceState.STOPPED, ServiceState.STARTING);
        }
        
        private void endStart() {
            this.serviceState.set(ServiceState.STARTED);
        }
        
        private boolean beginStop() {
            return this.serviceState.compareAndSet(ServiceState.STARTED, ServiceState.STOPPING);
        }
        
        private void endStop() {
            this.serviceState.set(ServiceState.STOPPED);
        }
        
        private boolean isStarted() {
            return this.serviceState.get() == ServiceState.STARTED;
        }
        
        private boolean isStartedOrStarting() {
            final ServiceState state = this.serviceState.get();
            return state == ServiceState.STARTED || state == ServiceState.STARTING;
        }
        
        private boolean isStoppedOrStopping() {
            final ServiceState state = this.serviceState.get();
            return state == ServiceState.STOPPED || state == ServiceState.STOPPING;
        }
        
        private boolean isStopped() {
            return this.serviceState.get() == ServiceState.STOPPED;
        }
    }
}
