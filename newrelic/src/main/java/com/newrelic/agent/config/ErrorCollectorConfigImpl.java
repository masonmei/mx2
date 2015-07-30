// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class ErrorCollectorConfigImpl extends BaseConfig implements ErrorCollectorConfig
{
    public static final String ENABLED = "enabled";
    public static final String COLLECT_ERRORS = "collect_errors";
    public static final String IGNORE_STATUS_CODES = "ignore_status_codes";
    public static final String IGNORE_ERRORS = "ignore_errors";
    public static final String IGNORE_ERROR_PRIORITY_KEY = "error_collector.ignoreErrorPriority";
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_COLLECT_ERRORS = true;
    public static final Set<Integer> DEFAULT_IGNORE_STATUS_CODES;
    public static final Set<String> DEFAULT_IGNORE_ERRORS;
    public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.error_collector.";
    private final boolean isEnabled;
    private final Set<String> ignoreErrors;
    private final Set<Integer> ignoreStatusCodes;
    
    private ErrorCollectorConfigImpl(final Map<String, Object> props) {
        super(props, "newrelic.config.error_collector.");
        this.isEnabled = this.initEnabled();
        this.ignoreErrors = this.initIgnoreErrors();
        this.ignoreStatusCodes = Collections.unmodifiableSet((Set<? extends Integer>)this.getIntegerSet("ignore_status_codes", ErrorCollectorConfigImpl.DEFAULT_IGNORE_STATUS_CODES));
    }
    
    private Set<String> initIgnoreErrors() {
        Collection<String> uniqueErrors = this.getUniqueStrings("ignore_errors");
        uniqueErrors = ((this.getProperty("ignore_errors") == null) ? ErrorCollectorConfigImpl.DEFAULT_IGNORE_ERRORS : uniqueErrors);
        final Set<String> result = new HashSet<String>(uniqueErrors.size());
        for (final String uniqueError : uniqueErrors) {
            result.add(uniqueError.replace('/', '.'));
        }
        return Collections.unmodifiableSet((Set<? extends String>)result);
    }
    
    private boolean initEnabled() {
        final boolean isEnabled = this.getProperty("enabled", true);
        final boolean canCollectErrors = this.getProperty("collect_errors", true);
        return isEnabled && canCollectErrors;
    }
    
    public Set<String> getIgnoreErrors() {
        return this.ignoreErrors;
    }
    
    public Set<Integer> getIgnoreStatusCodes() {
        return this.ignoreStatusCodes;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    static ErrorCollectorConfig createErrorCollectorConfig(Map<String, Object> settings) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new ErrorCollectorConfigImpl(settings);
    }
    
    static {
        DEFAULT_IGNORE_STATUS_CODES = Collections.unmodifiableSet((Set<? extends Integer>)new HashSet<Integer>(Arrays.asList(404)));
        DEFAULT_IGNORE_ERRORS = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("akka.actor.ActorKilledException")));
    }
}
