// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import com.newrelic.agent.config.AgentConfig;

public class AttributesFilter
{
    private final boolean captureRequestParameters;
    private final boolean captureMessageParameters;
    private final DestinationFilter errorFilter;
    private final DestinationFilter eventsFilter;
    private final DestinationFilter traceFilter;
    private final DestinationFilter browserFilter;
    
    public AttributesFilter(final AgentConfig config) {
        this(config, AttributesConfigUtil.DEFAULT_BROWSER_EXCLUDES, AttributesConfigUtil.DEFAULT_ERRORS_EXCLUDES, AttributesConfigUtil.DEFAULT_EVENTS_EXCLUDES, AttributesConfigUtil.DEFAULT_TRACES_EXCLUDES);
    }
    
    public AttributesFilter(final AgentConfig config, final String[] defaultExcludeBrowser, final String[] defaultExcludeErrors, final String[] defaultExcludeEvents, final String[] defaultExcludeTraces) {
        final List<String> rootExcludes = new ArrayList<String>();
        rootExcludes.addAll(AttributesConfigUtil.getBaseList(config, "attributes.exclude"));
        rootExcludes.addAll(AttributesConfigUtil.getBaseList(config, "ignored_params", "request.parameters."));
        rootExcludes.addAll(AttributesConfigUtil.getBaseList(config, "ignored_messaging_params", "message.parameters."));
        final List<String> rootIncludes = AttributesConfigUtil.getBaseList(config, "attributes.include");
        final boolean captureParams = AttributesConfigUtil.isCaptureAttributes(config);
        final boolean captureMessageParams = AttributesConfigUtil.isCaptureMessageAttributes(config);
        this.errorFilter = new DestinationFilter("error_collector", true, config, rootExcludes, rootIncludes, captureParams, captureMessageParams, defaultExcludeErrors, new String[] { "error_collector" });
        this.eventsFilter = new DestinationFilter("transaction_events", true, config, rootExcludes, rootIncludes, captureParams, captureMessageParams, defaultExcludeEvents, new String[] { "transaction_events", "analytics_events" });
        this.traceFilter = new DestinationFilter("transaction_tracer", true, config, rootExcludes, rootIncludes, captureParams, captureMessageParams, defaultExcludeTraces, new String[] { "transaction_tracer" });
        this.browserFilter = new DestinationFilter("browser_monitoring", false, config, rootExcludes, rootIncludes, captureParams, captureMessageParams, defaultExcludeBrowser, new String[] { "browser_monitoring" });
        final boolean enabled = this.errorFilter.isEnabled() || this.eventsFilter.isEnabled() || this.traceFilter.isEnabled();
        this.captureRequestParameters = this.captureAllParams(enabled, config.isHighSecurity(), captureParams, "request.parameters.");
        this.captureMessageParameters = this.captureAllParams(enabled, config.isHighSecurity(), captureMessageParams, "message.parameters.");
    }
    
    private boolean captureAllParams(final boolean enabled, final boolean highSecurity, final boolean captureParams, final String paramStart) {
        return enabled && !highSecurity && (captureParams || this.errorFilter.isPotentialConfigMatch(paramStart) || this.eventsFilter.isPotentialConfigMatch(paramStart) || this.traceFilter.isPotentialConfigMatch(paramStart) || this.browserFilter.isPotentialConfigMatch(paramStart));
    }
    
    public boolean captureRequestParams() {
        return this.captureRequestParameters;
    }
    
    public boolean captureMessageParams() {
        return this.captureMessageParameters;
    }
    
    public boolean isAttributesEnabledForErrors() {
        return this.errorFilter.isEnabled();
    }
    
    public boolean isAttributesEnabledForEvents() {
        return this.eventsFilter.isEnabled();
    }
    
    public boolean isAttributesEnabledForTraces() {
        return this.traceFilter.isEnabled();
    }
    
    public boolean isAttributesEnabledForBrowser() {
        return this.browserFilter.isEnabled();
    }
    
    public Map<String, ?> filterErrorAttributes(final Map<String, ?> values) {
        return this.errorFilter.filterAttributes(values);
    }
    
    public Map<String, ?> filterEventAttributes(final Map<String, ?> values) {
        return this.eventsFilter.filterAttributes(values);
    }
    
    public Map<String, ?> filterTraceAttributes(final Map<String, ?> values) {
        return this.traceFilter.filterAttributes(values);
    }
    
    public Map<String, ?> filterBrowserAttributes(final Map<String, ?> values) {
        return this.browserFilter.filterAttributes(values);
    }
}
