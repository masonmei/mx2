// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.Collection;
import com.newrelic.agent.application.PriorityApplicationName;
import com.newrelic.agent.config.AgentConfig;
import java.util.Collections;
import java.util.ArrayList;
import java.text.MessageFormat;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import com.newrelic.agent.service.AbstractService;

public class RPMServiceManagerImpl extends AbstractService implements RPMServiceManager
{
    private final IRPMService defaultRPMService;
    private final Map<String, IRPMService> appNameToRPMService;
    private final List<ConnectionListener> connectionListeners;
    private final ConnectionListener connectionListener;
    private volatile List<IRPMService> rpmServices;
    
    public RPMServiceManagerImpl() {
        super(RPMServiceManager.class.getSimpleName());
        this.appNameToRPMService = new ConcurrentHashMap<String, IRPMService>();
        this.connectionListeners = new CopyOnWriteArrayList<ConnectionListener>();
        this.connectionListener = new ConnectionListener() {
            public void connected(final IRPMService rpmService, final Map<String, Object> connectionInfo) {
                for (final ConnectionListener each : RPMServiceManagerImpl.this.connectionListeners) {
                    each.connected(rpmService, connectionInfo);
                }
            }
            
            public void disconnected(final IRPMService rpmService) {
                for (final ConnectionListener each : RPMServiceManagerImpl.this.connectionListeners) {
                    each.disconnected(rpmService);
                }
            }
        };
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        final String host = config.getHost();
        final String port = Integer.toString(config.getPort());
        this.getLogger().config(MessageFormat.format("Configured to connect to New Relic at {0}:{1}", host, port));
        this.defaultRPMService = this.createRPMService(config.getApplicationNames(), this.connectionListener);
        final List<IRPMService> list = new ArrayList<IRPMService>(1);
        list.add(this.defaultRPMService);
        this.rpmServices = Collections.unmodifiableList((List<? extends IRPMService>)list);
    }
    
    protected synchronized void doStart() throws Exception {
        for (final IRPMService rpmService : this.rpmServices) {
            rpmService.start();
        }
    }
    
    protected synchronized void doStop() throws Exception {
        for (final IRPMService rpmService : this.rpmServices) {
            rpmService.stop();
        }
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public void addConnectionListener(final ConnectionListener listener) {
        this.connectionListeners.add(listener);
    }
    
    public void removeConnectionListener(final ConnectionListener listener) {
        this.connectionListeners.remove(listener);
    }
    
    public IRPMService getRPMService() {
        return this.defaultRPMService;
    }
    
    public IRPMService getRPMService(final String appName) {
        if (appName == null || this.defaultRPMService.getApplicationName().equals(appName)) {
            return this.defaultRPMService;
        }
        return this.appNameToRPMService.get(appName);
    }
    
    public IRPMService getOrCreateRPMService(final PriorityApplicationName appName) {
        final IRPMService rpmService = this.getRPMService(appName.getName());
        if (rpmService != null) {
            return rpmService;
        }
        return this.createRPMServiceForAppName(appName.getName(), appName.getNames());
    }
    
    public IRPMService getOrCreateRPMService(final String appName) {
        final IRPMService rpmService = this.getRPMService(appName);
        if (rpmService != null) {
            return rpmService;
        }
        final List<String> appNames = new ArrayList<String>(1);
        appNames.add(appName);
        return this.createRPMServiceForAppName(appName, appNames);
    }
    
    private synchronized IRPMService createRPMServiceForAppName(final String appName, final List<String> appNames) {
        IRPMService rpmService = this.getRPMService(appName);
        if (rpmService == null) {
            rpmService = this.createRPMService(appNames, this.connectionListener);
            this.appNameToRPMService.put(appName, rpmService);
            final List<IRPMService> list = new ArrayList<IRPMService>(this.appNameToRPMService.size() + 1);
            list.addAll(this.appNameToRPMService.values());
            list.add(this.defaultRPMService);
            this.rpmServices = Collections.unmodifiableList((List<? extends IRPMService>)list);
            if (this.isStarted()) {
                try {
                    rpmService.start();
                }
                catch (Exception e) {
                    final String msg = MessageFormat.format("Error starting New Relic Service for {0}: {1}", rpmService.getApplicationName(), e);
                    this.getLogger().severe(msg);
                }
            }
        }
        return rpmService;
    }
    
    protected IRPMService createRPMService(final List<String> appNames, final ConnectionListener listener) {
        return new RPMService(appNames, listener);
    }
    
    public List<IRPMService> getRPMServices() {
        return this.rpmServices;
    }
}
