// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import java.util.logging.Level;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Map;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.service.AbstractService;

public class AttributesService extends AbstractService implements AgentConfigListener
{
    private final boolean enabled;
    private final String defaultAppName;
    private volatile AttributesFilter defaultFilter;
    private final Map<String, AttributesFilter> appNamesToFilters;
    
    public AttributesService() {
        super(AttributesService.class.getSimpleName());
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.enabled = (boolean)config.getValue("attributes.enabled", (Object)Boolean.TRUE);
        this.defaultAppName = config.getApplicationName();
        this.defaultFilter = new AttributesFilter(config);
        this.appNamesToFilters = (Map<String, AttributesFilter>)Maps.newConcurrentMap();
        ServiceFactory.getConfigService().addIAgentConfigListener(this);
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    protected void doStart() throws Exception {
        this.logDeprecatedSettings();
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getConfigService().removeIAgentConfigListener(this);
    }
    
    public boolean captureRequestParams(final String appName) {
        return this.getFilter(appName).captureRequestParams();
    }
    
    public boolean captureMessageParams(final String appName) {
        return this.getFilter(appName).captureMessageParams();
    }
    
    public boolean isAttributesEnabledForErrors(final String appName) {
        return this.getFilter(appName).isAttributesEnabledForErrors();
    }
    
    public boolean isAttributesEnabledForEvents(final String appName) {
        return this.getFilter(appName).isAttributesEnabledForEvents();
    }
    
    public boolean isAttributesEnabledForTraces(final String appName) {
        return this.getFilter(appName).isAttributesEnabledForTraces();
    }
    
    public boolean isAttributesEnabledForBrowser(final String appName) {
        return this.getFilter(appName).isAttributesEnabledForBrowser();
    }
    
    public Map<String, ?> filterErrorAttributes(final String appName, final Map<String, ?> values) {
        return this.getFilter(appName).filterErrorAttributes(values);
    }
    
    public Map<String, ?> filterEventAttributes(final String appName, final Map<String, ?> values) {
        return this.getFilter(appName).filterEventAttributes(values);
    }
    
    public Map<String, ?> filterTraceAttributes(final String appName, final Map<String, ?> values) {
        return this.getFilter(appName).filterTraceAttributes(values);
    }
    
    public Map<String, ?> filterBrowserAttributes(final String appName, final Map<String, ?> values) {
        return this.getFilter(appName).filterBrowserAttributes(values);
    }
    
    private AttributesFilter getFilter(final String appName) {
        if (appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultFilter;
        }
        final AttributesFilter filter = this.appNamesToFilters.get(appName);
        return (filter == null) ? this.defaultFilter : filter;
    }
    
    public void configChanged(final String appName, final AgentConfig agentConfig) {
        if (appName != null) {
            if (appName.equals(this.defaultAppName)) {
                this.defaultFilter = new AttributesFilter(agentConfig);
            }
            else {
                this.appNamesToFilters.put(appName, new AttributesFilter(agentConfig));
            }
        }
    }
    
    private void logDeprecatedSettings() {
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        if (config.getValue("analytics_events.capture_attributes") != null) {
            this.getLogger().log(Level.INFO, "The property analytics_events.capture_attributes is deprecated. Change to transaction_events.attributes.enabled.");
        }
        if (config.getValue("transaction_tracer.capture_attributes") != null) {
            this.getLogger().log(Level.INFO, "The property transaction_tracer.captures_attributes is deprecated. Change to transaction_tracer.attributes.enabled.");
        }
        if (config.getValue("browser_monitoring.capture_attributes") != null) {
            this.getLogger().log(Level.INFO, "The property browser_monitoring.capture_attributes is deprecated. Change to browser_monitoring.attributes.enabled.");
        }
        if (config.getValue("error_collector.capture_attributes") != null) {
            this.getLogger().log(Level.INFO, "The property error_collector.capture_attributes is deprecated. Change to error_collector.attributes.enabled.");
        }
        if (config.getValue("capture_params") != null) {
            this.getLogger().log(Level.INFO, "The property capture_params is deprecated. Request parameters are off by default. To enable request parameters, use attributes.include = request.parameters.*");
        }
        if (config.getValue("capture_messaging_params") != null) {
            this.getLogger().log(Level.INFO, "The property capture_messaging_params is deprecated. Message queue parameters are off by default. To enable message queue parameters, use attributes.include = message.parameters.*");
        }
        if (config.getValue("ignored_params") != null) {
            this.getLogger().log(Level.INFO, "The property ignored_params is deprecated. Change to attributes.exclude = request.parameters.${param_name}");
        }
        if (config.getValue("ignored_messaging_params") != null) {
            this.getLogger().log(Level.INFO, "The property ignored_messaging_params is deprecated. Change to attributes.exclude = message.parameters.${param_name}");
        }
    }
}
