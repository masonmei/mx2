// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import com.newrelic.agent.config.BaseConfig;
import java.util.List;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.config.AgentConfig;

public class AttributesConfigUtil
{
    protected static final String[] DEFAULT_BROWSER_EXCLUDES;
    protected static final String[] DEFAULT_EVENTS_EXCLUDES;
    protected static final String[] DEFAULT_ERRORS_EXCLUDES;
    protected static final String[] DEFAULT_TRACES_EXCLUDES;
    public static final String IGNORED_PARAMS = "ignored_params";
    public static final String IGNORED_MESSAGING_PARAMS = "ignored_messaging_params";
    protected static final String ATTS_ENABLED = "attributes.enabled";
    protected static final String ATTS_EXCLUDE = "attributes.exclude";
    protected static final String ATTS_INCLUDE = "attributes.include";
    protected static final String CAPTURE_ATTRIBUTES = ".capture_attributes";
    
    protected static boolean isCaptureAttributes(final AgentConfig config) {
        return getBooleanValue(config, "capture_params", Boolean.FALSE);
    }
    
    protected static boolean isCaptureMessageAttributes(final AgentConfig config) {
        return getBooleanValue(config, "capture_messaging_params", Boolean.FALSE);
    }
    
    protected static boolean isAttsEnabled(final AgentConfig config, final boolean defaultProp, final String... dest) {
        final Boolean enabledRoot = (Boolean)config.getValue("attributes.enabled");
        if (enabledRoot != null && !enabledRoot) {
            return enabledRoot;
        }
        boolean toEnable = false;
        Boolean destEnabled = null;
        for (final String current : dest) {
            destEnabled = getBooleanValue(config, current + "." + "attributes.enabled");
            if (destEnabled != null) {
                if (!destEnabled) {
                    return destEnabled;
                }
                toEnable = true;
            }
        }
        boolean toCapture = false;
        for (final String current2 : dest) {
            destEnabled = getBooleanValue(config, current2 + ".capture_attributes");
            if (destEnabled != null) {
                if (!destEnabled) {
                    return destEnabled;
                }
                toCapture = true;
            }
        }
        return toEnable || toCapture || defaultProp;
    }
    
    private static Boolean getBooleanValue(final AgentConfig config, final String value) {
        return getBooleanValue(config, value, null);
    }
    
    private static Boolean getBooleanValue(final AgentConfig config, final String value, final Object theDefault) {
        try {
            final Object inputObj = config.getValue(value, theDefault);
            if (inputObj != null) {
                if (inputObj instanceof Boolean) {
                    return (Boolean)inputObj;
                }
                if (inputObj instanceof String) {
                    return Boolean.parseBoolean((String)inputObj);
                }
            }
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINE, MessageFormat.format("The configuration property {0} should be a boolean but is not.", value));
        }
        return null;
    }
    
    protected static List<String> getBaseList(final AgentConfig config, final String key, final String prefix) {
        final Object val = config.getValue(key);
        if (val instanceof String) {
            return BaseConfig.getUniqueStringsFromString((String)val, ",", prefix);
        }
        if (val instanceof Collection) {
            return BaseConfig.getUniqueStringsFromCollection((Collection<?>)val, prefix);
        }
        return Collections.emptyList();
    }
    
    protected static List<String> getBaseList(final AgentConfig config, final String key) {
        return getBaseList(config, key, null);
    }
    
    protected static Set<String> getExcluded(final AgentConfig config, final List<String> baseList, final String dest) {
        final Set<String> output = Sets.newHashSet();
        output.addAll(baseList);
        output.addAll(getBaseList(config, dest + "." + "attributes.exclude"));
        return output;
    }
    
    protected static Set<String> getIncluded(final AgentConfig config, final List<String> baseList, final String dest) {
        final Set<String> output = Sets.newHashSet();
        output.addAll(baseList);
        output.addAll(getBaseList(config, dest + "." + "attributes.include"));
        return output;
    }
    
    static {
        DEFAULT_BROWSER_EXCLUDES = new String[] { "request.parameters.*", "message.parameters.*", "library.solr.*", "jvm.*", "httpResponseMessage", "request.headers.referer", "httpResponseCode", "host.displayName", "process.instanceName" };
        DEFAULT_EVENTS_EXCLUDES = new String[] { "request.parameters.*", "message.parameters.*", "library.solr.*", "jvm.*", "httpResponseMessage", "request.headers.referer" };
        DEFAULT_ERRORS_EXCLUDES = new String[0];
        DEFAULT_TRACES_EXCLUDES = new String[0];
    }
}
