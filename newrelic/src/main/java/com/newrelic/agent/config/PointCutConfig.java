// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.text.ParseException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Constructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.BaseConstructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.Loader;
import com.newrelic.agent.instrumentation.yaml.InstrumentationConstructor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Collections;
import com.newrelic.agent.instrumentation.yaml.PointCutFactory;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import com.newrelic.agent.extension.Extension;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.Yaml;

public class PointCutConfig
{
    private static String defaultMetricPrefix;
    private Yaml yaml;
    private final List<ExtensionClassAndMethodMatcher> pcList;
    
    public static Collection<ExtensionClassAndMethodMatcher> getExtensionPointCuts(final Extension extension, final Map instrumentation) {
        final Collection<ExtensionClassAndMethodMatcher> list = new ArrayList<ExtensionClassAndMethodMatcher>();
        if (instrumentation != null) {
            list.addAll(addInstrumentation(extension, instrumentation));
        }
        if (Agent.LOG.isLoggable(Level.FINEST)) {
            for (final ExtensionClassAndMethodMatcher pc : list.toArray(new ExtensionClassAndMethodMatcher[0])) {
                final String msg = MessageFormat.format("Extension instrumentation point: {0} {1}", pc.getClassMatcher(), pc.getMethodMatcher());
                Agent.LOG.finest(msg);
            }
        }
        return list;
    }
    
    private static Collection<ExtensionClassAndMethodMatcher> addInstrumentation(final Extension ext, final Map instrumentation) {
        try {
            PointCutConfig.defaultMetricPrefix = instrumentation.get("metric_prefix");
            PointCutConfig.defaultMetricPrefix = ((PointCutConfig.defaultMetricPrefix == null) ? "Custom" : PointCutConfig.defaultMetricPrefix);
            final Object pcConfig = instrumentation.get("pointcuts");
            final PointCutFactory pcFactory = new PointCutFactory(ext.getClassLoader(), PointCutConfig.defaultMetricPrefix, ext.getName());
            return pcFactory.getPointCuts(pcConfig);
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("An error occurred reading the pointcuts in extension {0} : {1}", ext.getName(), t.toString());
            Agent.LOG.severe(msg);
            Agent.LOG.log(Level.FINER, msg, t);
            return (Collection<ExtensionClassAndMethodMatcher>)Collections.emptyList();
        }
    }
    
    public PointCutConfig(final File[] files) {
        this.pcList = new ArrayList<ExtensionClassAndMethodMatcher>();
        if (null != files) {
            this.initYaml();
            for (final File file : files) {
                try {
                    final FileInputStream input = new FileInputStream(file);
                    this.loadYaml(input);
                    Agent.LOG.info(MessageFormat.format("Loaded custom instrumentation from {0}", file.getName()));
                }
                catch (FileNotFoundException e2) {
                    Agent.LOG.warning(MessageFormat.format("Could not open instrumentation file {0}. Please check that the file exists and has the correct permissions. ", file.getPath()));
                }
                catch (Exception e) {
                    Agent.LOG.log(Level.SEVERE, MessageFormat.format("Error loading YAML instrumentation from {0}. Please check the file's format.", file.getName()));
                    Agent.LOG.log(Level.FINER, "YAML error: ", e);
                }
            }
        }
    }
    
    public PointCutConfig(final InputStream input) {
        this.pcList = new ArrayList<ExtensionClassAndMethodMatcher>();
        this.initYaml();
        try {
            this.loadYaml(input);
        }
        catch (Exception e) {
            Agent.LOG.log(Level.SEVERE, "Error loading YAML instrumentation");
            Agent.LOG.log(Level.FINER, "Error: ", e);
        }
    }
    
    private void initYaml() {
        final Constructor constructor = new InstrumentationConstructor();
        final Loader loader = new Loader(constructor);
        this.yaml = new Yaml(loader);
    }
    
    private void loadYaml(final InputStream input) throws ParseException {
        if (null == input) {
            return;
        }
        final Object config = this.yaml.load(input);
        final PointCutFactory pcFactory = new PointCutFactory(this.getClass().getClassLoader(), "Custom", "CustomYaml");
        this.pcList.addAll(pcFactory.getPointCuts(config));
    }
    
    public List<ExtensionClassAndMethodMatcher> getPointCuts() {
        return this.pcList;
    }
}
