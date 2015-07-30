// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.text.MessageFormat;
import java.io.File;

public class ConfigFileHelper
{
    public static final String NEW_RELIC_YAML_FILE = "newrelic.yml";
    private static final String CONFIG_FILE_PROPERTY = "newrelic.config.file";
    private static final String NEW_RELIC_HOME_DIRECTORY_PROPERTY = "newrelic.home";
    private static final String NEW_RELIC_HOME_DIRECTORY_ENVIRONMENT_VARIABLE = "NEWRELIC_HOME";
    private static final String NEW_RELIC_DEBUG_PROPERTY = "newrelic.debug";
    private static final String[] SEARCH_DIRECTORIES;
    
    public static File findConfigFile() {
        File configFile = findFromProperty();
        if (configFile != null) {
            return configFile;
        }
        final File parentDir = getNewRelicDirectory();
        if (parentDir != null && Boolean.getBoolean("newrelic.debug")) {
            System.err.println(MessageFormat.format("New Relic home directory: {0}", parentDir));
        }
        if (parentDir != null) {
            configFile = findConfigFile(parentDir);
            if (configFile != null) {
                return configFile;
            }
        }
        return findConfigFileInWorkingDirectory();
    }
    
    public static File getNewRelicDirectory() {
        File newRelicDir = findHomeDirectory();
        if (newRelicDir == null) {
            newRelicDir = AgentJarHelper.getAgentJarDirectory();
        }
        return newRelicDir;
    }
    
    private static File findFromProperty() {
        final String filePath = System.getProperty("newrelic.config.file");
        if (filePath != null) {
            final File configFile = new File(filePath);
            if (configFile.exists()) {
                return configFile;
            }
            System.err.println(MessageFormat.format("The configuration file {0} specified with the {1} property does not exist", configFile.getAbsolutePath(), "newrelic.config.file"));
        }
        return null;
    }
    
    private static File findHomeDirectory() {
        File homeDir = findHomeDirectoryFromProperty();
        if (homeDir != null) {
            return homeDir;
        }
        homeDir = findHomeDirectoryFromEnvironmentVariable();
        if (homeDir != null) {
            return homeDir;
        }
        return null;
    }
    
    private static File findHomeDirectoryFromProperty() {
        final String filePath = System.getProperty("newrelic.home");
        if (filePath != null) {
            final File homeDir = new File(filePath);
            if (homeDir.exists()) {
                return homeDir;
            }
            System.err.println(MessageFormat.format("The directory {0} specified with the {1} property does not exist", homeDir.getAbsolutePath(), "newrelic.home"));
        }
        return null;
    }
    
    private static File findHomeDirectoryFromEnvironmentVariable() {
        final String filePath = System.getenv("NEWRELIC_HOME");
        if (filePath != null) {
            final File homeDir = new File(filePath);
            if (homeDir.exists()) {
                return homeDir;
            }
            System.err.println(MessageFormat.format("The directory {0} specified with the {1} environment variable does not exist", homeDir.getAbsolutePath(), "NEWRELIC_HOME"));
        }
        return null;
    }
    
    private static File findConfigFile(final File parentDirectory) {
        for (final String searchDir : ConfigFileHelper.SEARCH_DIRECTORIES) {
            final File configDir = new File(parentDirectory, searchDir);
            if (Boolean.getBoolean("newrelic.debug")) {
                System.err.println(MessageFormat.format("Searching for NewRelic configuration in directory {0}", configDir));
            }
            if (configDir.exists()) {
                final File configFile = new File(configDir, "newrelic.yml");
                if (configFile.exists()) {
                    return configFile;
                }
            }
        }
        return null;
    }
    
    private static File findConfigFileInWorkingDirectory() {
        final File configFile = new File("newrelic.yml");
        if (configFile.exists()) {
            return configFile;
        }
        return null;
    }
    
    static {
        SEARCH_DIRECTORIES = new String[] { ".", "conf", "config", "etc" };
    }
}
