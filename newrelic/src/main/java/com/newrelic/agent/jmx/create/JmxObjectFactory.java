// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import com.newrelic.agent.util.Annotations;
import com.newrelic.agent.jmx.metrics.JmxInit;
import com.newrelic.agent.jmx.metrics.BaseJmxInvokeValue;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import com.newrelic.agent.jmx.metrics.JMXMetricType;
import com.newrelic.agent.extension.Extension;
import com.newrelic.agent.jmx.JmxType;
import java.util.Map;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import java.util.List;
import com.newrelic.agent.config.JmxConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Collection;

public class JmxObjectFactory
{
    private final Collection<String> disabledJmxFrameworks;
    
    private JmxObjectFactory() {
        final JmxConfig jmxConfig = ServiceFactory.getConfigService().getDefaultAgentConfig().getJmxConfig();
        this.disabledJmxFrameworks = jmxConfig.getDisabledJmxFrameworks();
    }
    
    public static JmxObjectFactory createJmxFactory() {
        return new JmxObjectFactory();
    }
    
    public void getStartUpJmxObjects(final List<JmxGet> jmxGets, final List<JmxInvoke> jmxInvokes) {
        this.getStoredJmxObjects(jmxGets, jmxInvokes);
        this.getYmlJmxGets(jmxGets);
    }
    
    public void convertFramework(final JmxFrameworkValues framework, final List<JmxGet> jmxGets, final List<JmxInvoke> jmxInvokes) {
        if (framework != null) {
            if (this.isDisabled(framework)) {
                Agent.LOG.log(Level.INFO, MessageFormat.format("JMX Metrics for the {0} framework are disabled and therefore are not being loaded.", framework.getPrefix()));
            }
            else {
                this.convertToJmxGets(framework, jmxGets);
                this.convertToJmxInvoke(framework, jmxInvokes);
            }
        }
    }
    
    protected String getSafeObjectName(final String pObjectNameString) {
        return pObjectNameString;
    }
    
    private void createLogAddJmxGet(final String pObjectName, final String rootMetricName, final Map<JmxType, List<String>> pAttributesToType, final List<JmxGet> alreadyAdded, final Extension origin) {
        try {
            final JmxGet toAdd = new JmxSingleMBeanGet(pObjectName, rootMetricName, this.getSafeObjectName(pObjectName), pAttributesToType, origin);
            if (toAdd != null) {
                alreadyAdded.add(0, toAdd);
                if (Agent.LOG.isFineEnabled()) {
                    Agent.LOG.log(Level.FINER, MessageFormat.format("Adding JMX config: {0}", toAdd));
                }
            }
        }
        catch (Exception e) {
            Agent.LOG.log(Level.WARNING, "The JMX configuration is invalid and will not be added. Please check your JMX configuration file. The object name is " + pObjectName);
        }
    }
    
    private void createLogAddJmxGet(final JMXMetricType type, final String pObjectName, final String pRootMetricName, final List<JmxMetric> pMetrics, final JmxAttributeFilter attributeFilter, final JmxMetricModifier modifier, final List<JmxGet> alreadyAdded) {
        try {
            JmxGet toAdd = null;
            if (type == JMXMetricType.INCREMENT_COUNT_PER_BEAN) {
                toAdd = new JmxSingleMBeanGet(pObjectName, this.getSafeObjectName(pObjectName), pRootMetricName, pMetrics, attributeFilter, modifier);
            }
            else {
                toAdd = new JmxMultiMBeanGet(pObjectName, this.getSafeObjectName(pObjectName), pRootMetricName, pMetrics, attributeFilter, modifier);
            }
            if (toAdd != null) {
                alreadyAdded.add(0, toAdd);
                if (Agent.LOG.isFineEnabled()) {
                    Agent.LOG.log(Level.FINER, MessageFormat.format("Adding JMX config: {0}", toAdd));
                }
            }
        }
        catch (Exception e) {
            Agent.LOG.log(Level.WARNING, "The JMX configuration is invalid and will not be added. Please check your JMX configuration file. The object name is " + pObjectName);
        }
    }
    
    private void createLogAddJmxInvoke(final BaseJmxInvokeValue invoke, final List<JmxInvoke> alreadyAdded) {
        try {
            final JmxInvoke toAdd = new JmxInvoke(invoke.getObjectNameString(), this.getSafeObjectName(invoke.getObjectNameString()), invoke.getOperationName(), invoke.getParams(), invoke.getSignature());
            if (toAdd != null) {
                alreadyAdded.add(toAdd);
                if (Agent.LOG.isFineEnabled()) {
                    Agent.LOG.log(Level.FINER, MessageFormat.format("Adding JMX config: {0}", toAdd));
                }
            }
        }
        catch (Exception e) {
            Agent.LOG.log(Level.WARNING, "The JMX configuration is invalid and will not be added. Please check your JMX configuration file. The object name is " + invoke.getObjectNameString());
        }
    }
    
    private void getStoredJmxObjects(final List<JmxGet> gets, final List<JmxInvoke> invokes) {
        final Collection<Class<?>> classes = Annotations.getAnnotationClassesFromManifest(JmxInit.class, "com/newrelic/agent/jmx/values");
        if (classes != null) {
            for (final Class<?> clazz : classes) {
                this.convertFramework(this.loadJmxFrameworkValues(clazz), gets, invokes);
            }
        }
    }
    
    private boolean isDisabled(final JmxFrameworkValues current) {
        final String framework = current.getPrefix();
        return this.disabledJmxFrameworks.contains(framework);
    }
    
    private void convertToJmxInvoke(final JmxFrameworkValues framework, final List<JmxInvoke> alreadyAdded) {
        final List<BaseJmxInvokeValue> values = framework.getJmxInvokers();
        if (values != null) {
            for (final BaseJmxInvokeValue value : values) {
                this.createLogAddJmxInvoke(value, alreadyAdded);
            }
        }
    }
    
    private void convertToJmxGets(final JmxFrameworkValues framework, final List<JmxGet> alreadyAdded) {
        final List<BaseJmxValue> values = framework.getFrameworkMetrics();
        if (values != null) {
            for (final BaseJmxValue value : values) {
                this.createLogAddJmxGet(value.getType(), value.getObjectNameString(), value.getObjectMetricName(), value.getMetrics(), value.getAttributeFilter(), value.getModifier(), alreadyAdded);
            }
        }
    }
    
    private <T extends JmxFrameworkValues> JmxFrameworkValues loadJmxFrameworkValues(final Class<T> clazz) {
        try {
            return clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unable to create jmx framework values in class {0} : {1}", clazz.getName(), e.toString());
            Agent.LOG.severe(msg);
            Agent.LOG.log(Level.FINE, msg, e);
            return null;
        }
    }
    
    private void getYmlJmxGets(final List<JmxGet> alreadyAdded) {
        for (final Extension extension : ServiceFactory.getExtensionService().getInternalExtensions().values()) {
            this.addExtension(extension, alreadyAdded);
        }
    }
    
    public void addExtension(final Extension extension, final List<JmxGet> alreadyAdded) {
        if (extension.isEnabled()) {
            this.getStoredJmxGets(extension.getJmxConfig(), alreadyAdded, extension.getName(), extension);
        }
    }
    
    private void getStoredJmxGets(final Collection<JmxConfiguration> configs, final List<JmxGet> alreadyAdded, final String extensionName, final Extension origin) {
        for (final JmxConfiguration parser : configs) {
            final boolean isEnabled = parser.getEnabled();
            if (isEnabled) {
                final String objectNameString = parser.getObjectName();
                if (objectNameString == null || objectNameString.trim().length() == 0) {
                    Agent.LOG.log(Level.WARNING, "Not recording JMX metric because the object name is null or empty in extension " + extensionName);
                }
                else {
                    final Map<JmxType, List<String>> attrs = parser.getAttrs();
                    if (attrs == null || attrs.size() == 0) {
                        Agent.LOG.log(Level.WARNING, MessageFormat.format("Not recording JMX metric with object name {0} in extension {1} because there are no attributes.", objectNameString, extensionName));
                    }
                    else {
                        this.createLogAddJmxGet(objectNameString, parser.getRootMetricName(), attrs, alreadyAdded, origin);
                    }
                }
            }
        }
    }
}
