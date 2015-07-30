// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import java.io.File;
import java.util.Map;

public class ConfigServiceFactory
{
    public static ConfigService createConfigService(final AgentConfig config, final Map<String, Object> localSettings) {
        return new ConfigServiceImpl(config, null, localSettings);
    }
    
    public static ConfigService createConfigServiceUsingSettings(final Map<String, Object> settings) {
        return new ConfigServiceImpl(AgentConfigImpl.createAgentConfig(settings), null, settings);
    }
    
    public static ConfigService createConfigService() throws ConfigurationException {
        final File configFile = getConfigFile();
        final Map<String, Object> configSettings = getConfigurationFileSettings(configFile);
        final AgentConfig config = AgentConfigImpl.createAgentConfig(configSettings);
        validateConfig(config);
        return new ConfigServiceImpl(config, configFile, configSettings);
    }
    
    public static Map<String, Object> getConfigurationFileSettings(final File configFile) throws ConfigurationException {
        String msg = MessageFormat.format("New Relic Agent: Loading configuration file \"{0}\"", configFile.getPath());
        Agent.LOG.info(msg);
        try {
            return AgentConfigHelper.getConfigurationFileSettings(configFile);
        }
        catch (Exception e) {
            msg = MessageFormat.format("An error occurred reading the configuration file {0}. Check the permissions and format of the file. - {1}", configFile.getAbsolutePath(), e.toString());
            throw new ConfigurationException(msg, e);
        }
    }
    
    private static File getConfigFile() throws ConfigurationException {
        final File configFile = ConfigFileHelper.findConfigFile();
        if (configFile == null) {
            throw new ConfigurationException("Failed to find the configuration file");
        }
        return configFile;
    }
    
    private static void validateConfig(final AgentConfig config) throws ConfigurationException {
        if (config.getApplicationName() == null) {
            throw new ConfigurationException("The agent requires an application name.  Check the app_name setting in newrelic.yml");
        }
    }
}
