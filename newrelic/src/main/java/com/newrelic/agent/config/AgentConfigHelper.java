// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.List;
import com.newrelic.agent.instrumentation.methodmatchers.InvalidMethodDescriptor;
import com.newrelic.agent.errors.ExceptionHandlerSignature;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.SequenceNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Construct;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Constructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.BaseConstructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.Loader;
import com.newrelic.agent.deps.org.yaml.snakeyaml.Yaml;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.util.Collections;
import com.newrelic.agent.Agent;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.io.File;

public class AgentConfigHelper
{
    public static final String NEWRELIC_ENVIRONMENT = "newrelic.environment";
    private static final String JAVA_ENVIRONMENT = "JAVA_ENV";
    private static final String PRODUCTION_ENVIRONMENT = "production";
    
    public static Map<String, Object> getConfigurationFileSettings(final File configFile) throws Exception {
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
            final Map<String, Object> configuration = parseConfiguration(is);
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException ex) {}
            return configuration;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    private static Map<String, Object> parseConfiguration(final InputStream is) throws Exception {
        final String env = getEnvironment();
        try {
            final Map<String, Object> allConfig = (Map<String, Object>)createYaml().load(is);
            if (allConfig == null) {
                Agent.LOG.info("The configuration file is empty");
                return Collections.emptyMap();
            }
            Map<String, Object> props = allConfig.get(env);
            if (props == null) {
                props = allConfig.get("common");
            }
            if (props == null) {
                throw new Exception(MessageFormat.format("Unable to find configuration named {0}", env));
            }
            return props;
        }
        catch (Exception e) {
            Agent.LOG.log(Level.SEVERE, MessageFormat.format("Unable to parse configuration file. Please validate the yaml: {0}", e.toString()), e);
            throw e;
        }
    }
    
    private static String getEnvironment() {
        try {
            String env = System.getProperty("newrelic.environment");
            env = ((env == null) ? System.getenv("JAVA_ENV") : env);
            return (env == null) ? "production" : env;
        }
        catch (Throwable t) {
            return "production";
        }
    }
    
    private static Yaml createYaml() {
        final Constructor constructor = new ExtensionConstructor();
        final Loader loader = new Loader(constructor);
        return new Yaml(loader);
    }
    
    private static class ExtensionConstructor extends Constructor
    {
        public ExtensionConstructor() {
            this.yamlConstructors.put("!exception_handler", new Construct() {
                public Object construct(final Node node) {
                    final List<?> args = ExtensionConstructor.this.constructSequence((SequenceNode)node);
                    try {
                        return new ExceptionHandlerSignature((String)args.get(0), (String)args.get(1), (String)args.get(2));
                    }
                    catch (InvalidMethodDescriptor e) {
                        return e;
                    }
                }
            });
        }
    }
}
