// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.browser;

import java.util.HashMap;
import com.newrelic.agent.config.AgentConfigFactory;
import com.newrelic.agent.config.AgentConfig;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Map;

public class BrowserConfigFactory
{
    public static IBrowserConfig createBrowserConfig(final String appName, final Map<String, Object> serverData) {
        try {
            final IBrowserConfig browserConfig = createTheBrowserConfig(appName, serverData);
            final AgentConfig agentConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
            final String autoInstrument = agentConfig.getBrowserMonitoringConfig().isAutoInstrumentEnabled() ? " with auto instrumentation" : "";
            final String msg = MessageFormat.format("Real user monitoring is enabled{0} for application \"{1}\"", autoInstrument, appName);
            Agent.LOG.info(msg);
            return browserConfig;
        }
        catch (Exception e) {
            final String msg2 = MessageFormat.format("Unable to configure application \"{0}\" for Real User Monitoring: {1}", appName, e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg2, e);
            }
            else {
                Agent.LOG.finer(msg2);
            }
            Agent.LOG.info(MessageFormat.format("Real user monitoring is not enabled for application \"{0}\"", appName));
            return null;
        }
    }
    
    private static IBrowserConfig createTheBrowserConfig(final String appName, final Map<String, Object> serverData) throws Exception {
        final Map<String, Object> settings = createMap();
        mergeBrowserSettings(settings, serverData);
        final Map<String, Object> agentData = AgentConfigFactory.getAgentData(serverData);
        mergeBrowserSettings(settings, agentData);
        return BrowserConfig.createBrowserConfig(appName, settings);
    }
    
    private static void mergeBrowserSettings(final Map<String, Object> settings, final Map<String, Object> data) {
        if (data == null) {
            return;
        }
        mergeSetting("browser_key", settings, data);
        mergeSetting("browser_monitoring.loader_version", settings, data);
        mergeSetting("js_agent_loader", settings, data);
        mergeSetting("js_agent_file", settings, data);
        mergeSetting("beacon", settings, data);
        mergeSetting("error_beacon", settings, data);
        mergeSetting("application_id", settings, data);
    }
    
    private static void mergeSetting(final String currentSetting, final Map<String, Object> settings, final Map<String, Object> data) {
        final Object val = data.get(currentSetting);
        if (val != null) {
            settings.put(currentSetting, val);
        }
    }
    
    private static Map<String, Object> createMap() {
        return new HashMap<String, Object>();
    }
}
