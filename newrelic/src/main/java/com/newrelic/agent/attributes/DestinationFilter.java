// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import com.newrelic.agent.deps.com.google.common.base.Predicate;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import java.util.Collections;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Set;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.List;
import com.newrelic.agent.config.AgentConfig;

public class DestinationFilter
{
    private final boolean isEnabled;
    private final DestinationPredicate filter;
    
    public DestinationFilter(final String mainNameForFilter, final boolean defaultInclude, final AgentConfig config, final List<String> confixExcludes, final List<String> configIncludes, final boolean captureParams, final boolean captureMessageParams, final String[] defaultExclude, final String... namesForIsEnabled) {
        this.isEnabled = AttributesConfigUtil.isAttsEnabled(config, defaultInclude, namesForIsEnabled);
        Agent.LOG.log(Level.FINE, "Attributes are {0} for {1}", new Object[] { this.isEnabled ? "enabled" : "disabled", mainNameForFilter });
        this.filter = getDestinationPredicate(this.isEnabled, config, confixExcludes, configIncludes, mainNameForFilter, updateDefaults(captureParams, captureMessageParams, defaultExclude));
    }
    
    private static DestinationPredicate getDestinationPredicate(final boolean isEnabled, final AgentConfig config, final List<String> rootExcludes, final List<String> rootIncludes, final String name, final Set<String> defaultExclude) {
        if (isEnabled) {
            final Set<String> configExclude = AttributesConfigUtil.getExcluded(config, rootExcludes, name);
            final Set<String> configInclude = AttributesConfigUtil.getIncluded(config, rootIncludes, name);
            return new DefaultDestinationPredicate(name, configExclude, configInclude, defaultExclude, getMandatoryExcludes(config.isHighSecurity()));
        }
        return new DisabledDestinationPredicate();
    }
    
    private static Set<String> getMandatoryExcludes(final boolean highSecurity) {
        if (highSecurity) {
            return Sets.newHashSet("request.parameters.*", "message.parameters.*");
        }
        return Collections.emptySet();
    }
    
    private static Set<String> updateDefaults(final boolean captureParams, final boolean captureMessageParams, final String[] defaultExclude) {
        final Set<String> defaultExc = Sets.newHashSet(defaultExclude);
        if (!captureParams) {
            defaultExc.add("request.parameters.*");
        }
        if (!captureMessageParams) {
            defaultExc.add("message.parameters.*");
        }
        return defaultExc;
    }
    
    protected boolean isPotentialConfigMatch(final String paramStart) {
        return this.filter.isPotentialConfigMatch(paramStart);
    }
    
    protected boolean isEnabled() {
        return this.isEnabled;
    }
    
    protected Map<String, ?> filterAttributes(final Map<String, ?> values) {
        return this.filterAttributes(values, this.filter);
    }
    
    private Map<String, ?> filterAttributes(final Map<String, ?> values, final DestinationPredicate predicate) {
        return (this.isEnabled && values != null && !values.isEmpty()) ? Maps.filterKeys(values, predicate) : Collections.emptyMap();
    }
}
