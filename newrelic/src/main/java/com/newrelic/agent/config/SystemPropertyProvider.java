// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Properties;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class SystemPropertyProvider
{
    private static final String HEROKU_PREFIX = "NEW_RELIC_";
    private static final String HEROKU_LICENSE_KEY = "NEW_RELIC_LICENSE_KEY";
    private static final String HEROKU_APP_NAME = "NEW_RELIC_APP_NAME";
    private static final String HEROKU_LOG = "NEW_RELIC_LOG";
    private static final String HEROKU_HOST_DISPLAY_NAME = "NEW_RELIC_PROCESS_HOST_DISPLAY_NAME";
    private static final String LICENSE_KEY = "newrelic.config.license_key";
    private static final String APP_NAME = "newrelic.config.app_name";
    private static final String LOG_FILE_NAME = "newrelic.config.log_file_name";
    private static final String HOST_DISPLAY_NAME = "newrelic.config.process_host.display_name";
    private static final String NEW_RELIC_SYSTEM_PROPERTY_ROOT = "newrelic.";
    private final Map<String, String> herokuEnvVars;
    private final Map<String, String> herokuEnvVarsFlattenedMapping;
    private final Map<String, String> newRelicSystemProps;
    private final Map<String, Object> newRelicPropsWithoutPrefix;
    private final SystemProps systemProps;
    
    public SystemPropertyProvider() {
        this(SystemProps.getSystemProps());
    }
    
    public SystemPropertyProvider(final SystemProps sysProps) {
        this.systemProps = sysProps;
        this.herokuEnvVars = this.initHerokuEnvVariables();
        this.herokuEnvVarsFlattenedMapping = this.initHerokuFlattenedEnvVariables();
        this.newRelicSystemProps = this.initNewRelicSystemProperties();
        this.newRelicPropsWithoutPrefix = this.createNewRelicSystemPropertiesWithoutPrefix();
    }
    
    private Map<String, String> initHerokuEnvVariables() {
        final Map<String, String> envVars = new HashMap<String, String>(6);
        envVars.put("newrelic.config.license_key", this.getenv("NEW_RELIC_LICENSE_KEY"));
        envVars.put("newrelic.config.app_name", this.getenv("NEW_RELIC_APP_NAME"));
        envVars.put("newrelic.config.log_file_name", this.getenv("NEW_RELIC_LOG"));
        envVars.put("newrelic.config.process_host.display_name", this.getenv("NEW_RELIC_PROCESS_HOST_DISPLAY_NAME"));
        return envVars;
    }
    
    private Map<String, String> initHerokuFlattenedEnvVariables() {
        final Map<String, String> envVars = new HashMap<String, String>(6);
        envVars.put("NEW_RELIC_LICENSE_KEY", "newrelic.config.license_key");
        envVars.put("NEW_RELIC_APP_NAME", "newrelic.config.app_name");
        envVars.put("NEW_RELIC_LOG", "newrelic.config.log_file_name");
        envVars.put("NEW_RELIC_PROCESS_HOST_DISPLAY_NAME", "newrelic.config.process_host.display_name");
        return envVars;
    }
    
    private Map<String, String> initNewRelicSystemProperties() {
        final Map<String, String> nrProps = (Map<String, String>)Maps.newHashMap();
        try {
            for (final Map.Entry<Object, Object> entry : this.systemProps.getAllSystemPropertes().entrySet()) {
                final String key = entry.getKey().toString();
                if (key.startsWith("newrelic.")) {
                    final String val = entry.getValue().toString();
                    nrProps.put(key, val);
                }
            }
        }
        catch (SecurityException t) {
            Agent.LOG.log(Level.FINE, "Unable to get system properties");
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)nrProps);
    }
    
    private Map<String, Object> createNewRelicSystemPropertiesWithoutPrefix() {
        final Map<String, Object> nrProps = (Map<String, Object>)Maps.newHashMap();
        this.addNewRelicSystemProperties(nrProps, (Set<Map.Entry>)this.systemProps.getAllSystemPropertes().entrySet());
        this.addNewRelicEnvProperties(nrProps, (Set<Map.Entry>)this.systemProps.getAllEnvProperties().entrySet());
        return Collections.unmodifiableMap((Map<? extends String, ?>)nrProps);
    }
    
    private void addNewRelicSystemProperties(final Map<String, Object> nrProps, final Set<Map.Entry> entrySet) {
        for (final Map.Entry<?, ?> entry : entrySet) {
            final String key = entry.getKey().toString();
            if (key.startsWith("newrelic.config.")) {
                this.addPropertyWithoutSystemPropRoot(nrProps, key, entry.getValue());
            }
        }
    }
    
    private void addNewRelicEnvProperties(final Map<String, Object> nrProps, final Set<Map.Entry> entrySet) {
        for (final Map.Entry<?, ?> entry : entrySet) {
            final String key = entry.getKey().toString();
            if (key.startsWith("newrelic.config.")) {
                this.addPropertyWithoutSystemPropRoot(nrProps, key, entry.getValue());
            }
            else {
                final String keyToUse = this.herokuEnvVarsFlattenedMapping.get(key);
                if (keyToUse == null) {
                    continue;
                }
                this.addPropertyWithoutSystemPropRoot(nrProps, keyToUse, entry.getValue());
            }
        }
    }
    
    private void addPropertyWithoutSystemPropRoot(final Map<String, Object> nrProps, String key, final Object value) {
        final String val = value.toString();
        key = key.substring("newrelic.config.".length());
        nrProps.put(key, val);
    }
    
    public String getEnvironmentVariable(final String prop) {
        final String val = this.herokuEnvVars.get(prop);
        if (val != null) {
            return val;
        }
        return this.getenv(prop);
    }
    
    public String getSystemProperty(final String prop) {
        return this.systemProps.getSystemProperty(prop);
    }
    
    private String getenv(final String key) {
        return this.systemProps.getenv(key);
    }
    
    public Map<String, String> getNewRelicSystemProperties() {
        return this.newRelicSystemProps;
    }
    
    public Map<String, Object> getNewRelicPropertiesWithoutPrefix() {
        return this.newRelicPropsWithoutPrefix;
    }
    
    protected abstract static class SystemProps
    {
        static SystemProps getSystemProps() {
            try {
                System.getProperties().get("test");
                System.getenv("test");
                return new SystemProps() {
                    String getSystemProperty(final String prop) {
                        return System.getProperty(prop);
                    }
                    
                    String getenv(final String key) {
                        return System.getenv(key);
                    }
                    
                    Properties getAllSystemPropertes() {
                        return System.getProperties();
                    }
                    
                    Map<String, String> getAllEnvProperties() {
                        return System.getenv();
                    }
                };
            }
            catch (SecurityException e) {
                Agent.LOG.error("Unable to access system properties because of a security exception.");
                return new SystemProps() {
                    String getSystemProperty(final String prop) {
                        return null;
                    }
                    
                    String getenv(final String key) {
                        return null;
                    }
                    
                    Properties getAllSystemPropertes() {
                        return new Properties();
                    }
                    
                    Map<String, String> getAllEnvProperties() {
                        return Collections.emptyMap();
                    }
                };
            }
        }
        
        abstract String getSystemProperty(final String p0);
        
        abstract String getenv(final String p0);
        
        abstract Properties getAllSystemPropertes();
        
        abstract Map<String, String> getAllEnvProperties();
    }
}
