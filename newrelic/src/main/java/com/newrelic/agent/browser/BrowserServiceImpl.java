// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.browser;

import java.util.Map;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.ConnectionListener;
import com.newrelic.agent.service.AbstractService;

public class BrowserServiceImpl extends AbstractService implements BrowserService, ConnectionListener
{
    private final ConcurrentMap<String, IBrowserConfig> browserConfigs;
    private volatile IBrowserConfig defaultBrowserConfig;
    private final String defaultAppName;
    
    public BrowserServiceImpl() {
        super(BrowserService.class.getSimpleName());
        this.browserConfigs = new ConcurrentHashMap<String, IBrowserConfig>();
        this.defaultBrowserConfig = null;
        this.defaultAppName = ServiceFactory.getConfigService().getDefaultAgentConfig().getApplicationName();
    }
    
    protected void doStart() throws Exception {
        ServiceFactory.getRPMServiceManager().addConnectionListener(this);
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getRPMServiceManager().removeConnectionListener(this);
    }
    
    public IBrowserConfig getBrowserConfig(final String appName) {
        if (appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultBrowserConfig;
        }
        return this.browserConfigs.get(appName);
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public void connected(final IRPMService rpmService, final Map<String, Object> serverData) {
        final String appName = rpmService.getApplicationName();
        final IBrowserConfig browserConfig = BrowserConfigFactory.createBrowserConfig(appName, serverData);
        if (appName == null || appName.equals(this.defaultAppName)) {
            this.defaultBrowserConfig = browserConfig;
        }
        else if (browserConfig == null) {
            this.browserConfigs.remove(appName);
        }
        else {
            this.browserConfigs.put(appName, browserConfig);
        }
    }
    
    public void disconnected(final IRPMService rpmService) {
    }
}
