// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import com.newrelic.agent.extension.Extension;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Iterator;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.Attribute;
import javax.management.ObjectInstance;
import java.util.Map;
import javax.management.ObjectName;
import java.util.HashMap;
import javax.management.QueryExp;
import java.util.Collection;
import java.text.MessageFormat;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.Agent;
import java.util.logging.Level;
import javax.management.MBeanServerFactory;
import java.util.Collections;
import com.newrelic.agent.config.JmxConfig;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.LinkedList;
import java.util.HashSet;
import javax.management.MBeanServer;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import java.util.Queue;
import com.newrelic.agent.jmx.create.JmxInvoke;
import com.newrelic.agent.jmx.create.JmxGet;
import java.util.List;
import com.newrelic.agent.jmx.create.JmxObjectFactory;
import java.util.Set;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class JmxService extends AbstractService implements HarvestListener
{
    private static final int INVOKE_ERROR_COUNT_MAX = 5;
    private static final String J2EE_STATS_ATTRIBUTE_PROCESSOR_CLASS_NAME = "com.newrelic.agent.jmx.J2EEStatsAttributeProcessor";
    private static final String WEBSPHERE_STATS_ATTRIBUTE_PROCESSOR_CLASS_NAME = "com.newrelic.agent.jmx.WebSphereStatsAttributeProcessor";
    private final boolean enabled;
    private final boolean createMBeanServerIfNecessary;
    private final Set<JmxAttributeProcessor> jmxAttributeProcessors;
    private final JmxObjectFactory jmxMetricFactory;
    private final List<JmxGet> jmxGets;
    private final List<JmxInvoke> jmxInvokes;
    private final Queue<JmxFrameworkValues> toBeAdded;
    private final Set<MBeanServer> alwaysIncludeMBeanServers;
    private final Set<MBeanServer> toRemoveMBeanServers;
    
    public JmxService() {
        super(JmxService.class.getSimpleName());
        this.jmxAttributeProcessors = new HashSet<JmxAttributeProcessor>();
        this.jmxGets = new LinkedList<JmxGet>();
        this.jmxInvokes = new LinkedList<JmxInvoke>();
        this.toBeAdded = new ConcurrentLinkedQueue<JmxFrameworkValues>();
        this.alwaysIncludeMBeanServers = new CopyOnWriteArraySet<MBeanServer>();
        this.toRemoveMBeanServers = new CopyOnWriteArraySet<MBeanServer>();
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        final JmxConfig jmxConfig = config.getJmxConfig();
        this.enabled = jmxConfig.isEnabled();
        this.createMBeanServerIfNecessary = jmxConfig.isCreateMbeanServer();
        this.jmxMetricFactory = JmxObjectFactory.createJmxFactory();
    }
    
    public List<JmxGet> getConfigurations() {
        return Collections.unmodifiableList((List<? extends JmxGet>)this.jmxGets);
    }
    
    public void addJmxAttributeProcessor(final JmxAttributeProcessor attributeProcessor) {
        this.jmxAttributeProcessors.add(attributeProcessor);
    }
    
    protected void doStart() {
        if (this.enabled) {
            this.jmxMetricFactory.getStartUpJmxObjects(this.jmxGets, this.jmxInvokes);
            if (this.jmxGets.size() > 0) {
                ServiceFactory.getHarvestService().addHarvestListener(this);
            }
            this.addJmxAttributeProcessor(JmxAttributeProcessorWrapper.createInstance("com.newrelic.agent.jmx.J2EEStatsAttributeProcessor"));
            this.addJmxAttributeProcessor(JmxAttributeProcessorWrapper.createInstance("com.newrelic.agent.jmx.WebSphereStatsAttributeProcessor"));
        }
    }
    
    public void createMBeanServerIfNeeded() {
        if (System.getProperty("com.sun.management.jmxremote") == null && MBeanServerFactory.findMBeanServer(null).isEmpty() && this.createMBeanServerIfNecessary) {
            try {
                MBeanServerFactory.createMBeanServer();
                this.getLogger().log(Level.FINE, "Created a default MBeanServer");
            }
            catch (Exception e) {
                Agent.LOG.severe("The JMX Service was unable to create a default mbean server");
            }
        }
    }
    
    public final boolean isEnabled() {
        return this.enabled;
    }
    
    public void addJmxFrameworkValues(final JmxFrameworkValues jmxValues) {
        if (this.enabled) {
            this.toBeAdded.add(jmxValues);
        }
    }
    
    protected void doStop() {
        this.jmxGets.clear();
        this.jmxInvokes.clear();
        this.jmxAttributeProcessors.clear();
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.log(Level.FINER, MessageFormat.format("Harvesting JMX metrics for {0}", appName));
        }
        try {
            this.process(statsEngine);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unexpected error querying MBeans in JMX service: ", e.toString());
            this.getLogger().finer(msg);
        }
    }
    
    public void afterHarvest(final String appName) {
    }
    
    public void setJmxServer(final MBeanServer server) {
        if (server != null && !this.alwaysIncludeMBeanServers.contains(server)) {
            Agent.LOG.log(Level.FINE, "JMX Service : MBeanServer of type {0} was added.", new Object[] { server.getClass().getName() });
            this.alwaysIncludeMBeanServers.add(server);
        }
    }
    
    public void removeJmxServer(final MBeanServer serverToRemove) {
        if (serverToRemove != null) {
            Agent.LOG.log(Level.FINE, "JMX Service : MBeanServer of type {0} was removed.", new Object[] { serverToRemove.getClass().getName() });
            this.toRemoveMBeanServers.add(serverToRemove);
        }
    }
    
    private void process(final StatsEngine statsEngine, final Collection<MBeanServer> srvrList, final JmxGet config, final Set<String> metricNames) {
        final ObjectName name = config.getObjectName();
        if (name == null) {
            return;
        }
        for (final MBeanServer server : srvrList) {
            try {
                final Set<ObjectInstance> queryMBeans = server.queryMBeans(name, null);
                this.getLogger().finer(MessageFormat.format("JMX Service : MBeans query {0}, matches {1}", name, queryMBeans.size()));
                final Map<ObjectName, Map<String, Float>> mbeanToAttValues = new HashMap<ObjectName, Map<String, Float>>();
                for (final ObjectInstance instance : queryMBeans) {
                    final ObjectName actualName = instance.getObjectName();
                    final String rootMetricName = config.getRootMetricName(actualName, server);
                    final Collection<String> attributes = config.getAttributes();
                    final Map<String, Float> values = new HashMap<String, Float>();
                    for (final String attr : attributes) {
                        try {
                            final String[] compNames = attr.split("\\.");
                            final Object attrObj = server.getAttribute(instance.getObjectName(), compNames[0]);
                            if (attrObj instanceof Attribute) {
                                this.recordJmxValue(statsEngine, instance, (Attribute)attrObj, rootMetricName, attr, values);
                            }
                            else if (attrObj instanceof CompositeDataSupport) {
                                if (compNames.length == 2) {
                                    this.recordJmxValue(statsEngine, instance, new Attribute(attr, ((CompositeDataSupport)attrObj).get(compNames[1])), rootMetricName, attr, values);
                                }
                                else {
                                    this.getLogger().fine(MessageFormat.format("Found CompositeDataSupport object for {0}, but no object attribute specified, correct syntax is object.attribute", attr));
                                }
                            }
                            else {
                                this.recordJmxValue(statsEngine, instance, new Attribute(attr, attrObj), rootMetricName, attr, values);
                            }
                        }
                        catch (Exception e) {
                            this.getLogger().fine(MessageFormat.format("An error occurred fetching JMX attribute {0} for metric {1}", attr, name));
                            this.getLogger().log(Level.FINEST, "JMX error", e);
                        }
                    }
                    if (!values.isEmpty()) {
                        mbeanToAttValues.put(actualName, values);
                    }
                }
                config.recordStats(statsEngine, mbeanToAttValues, server);
            }
            catch (Exception e2) {
                this.getLogger().fine(MessageFormat.format("An error occurred fetching JMX object matching name {0}", name));
                this.getLogger().log(Level.FINEST, "JMX error", e2);
            }
        }
    }
    
    private void runThroughAndRemoveInvokes(final Collection<MBeanServer> srvrList) {
        if (this.jmxInvokes.size() > 0) {
            final Iterator<JmxInvoke> invokes = this.jmxInvokes.iterator();
            while (invokes.hasNext()) {
                final JmxInvoke current = invokes.next();
                if (this.handleInvoke(srvrList, current)) {
                    invokes.remove();
                }
                else {
                    current.incrementErrorCount();
                    if (current.getErrorCount() < 5) {
                        continue;
                    }
                    invokes.remove();
                }
            }
        }
    }
    
    private boolean handleInvoke(final Collection<MBeanServer> srvrList, final JmxInvoke invoke) {
        final ObjectName name = invoke.getObjectName();
        if (name == null) {
            return true;
        }
        boolean isSuccess = false;
        for (final MBeanServer server : srvrList) {
            if (this.invoke(server, invoke)) {
                isSuccess = true;
            }
        }
        return isSuccess;
    }
    
    private boolean invoke(final MBeanServer server, final JmxInvoke current) {
        try {
            server.invoke(current.getObjectName(), current.getOperationName(), current.getParams(), current.getSignature());
            this.getLogger().fine(MessageFormat.format("Successfully invoked JMX server for {0}", current.getObjectNameString()));
            return true;
        }
        catch (Exception e) {
            this.getLogger().fine(MessageFormat.format("An error occurred invoking JMX server for {0}", current.getObjectNameString()));
            this.getLogger().log(Level.FINEST, "JMX error", e);
            return false;
        }
    }
    
    private void recordJmxValue(final StatsEngine statsEngine, final ObjectInstance instance, final Attribute attribute, final String rootMetric, final String attName, final Map<String, Float> values) {
        if (this.recordCustomJmxValue(statsEngine, instance, attribute, rootMetric, values)) {
            return;
        }
        this.recordNonCustomJmxValue(instance, attribute, attName, values);
    }
    
    private boolean recordCustomJmxValue(final StatsEngine statsEngine, final ObjectInstance instance, final Attribute attribute, final String metricName, final Map<String, Float> values) {
        for (final JmxAttributeProcessor processor : this.jmxAttributeProcessors) {
            if (processor.process(statsEngine, instance, attribute, metricName, values)) {
                return true;
            }
        }
        return false;
    }
    
    private void recordNonCustomJmxValue(final ObjectInstance instance, final Attribute attribute, final String attName, final Map<String, Float> values) {
        final Object value = attribute.getValue();
        Number num = null;
        if (value instanceof Number) {
            num = (Number)value;
        }
        else if (value instanceof Boolean) {
            num = (((Boolean)value) ? 1 : 0);
        }
        else if (value != null) {
            try {
                num = Float.parseFloat(value.toString());
            }
            catch (NumberFormatException ex) {}
        }
        if (num != null) {
            this.getLogger().finer(MessageFormat.format("Recording JMX metric {0} : {1}", attName, value));
            values.put(attName, num.floatValue());
        }
        else if (value == null) {
            this.getLogger().fine(MessageFormat.format("MBean {0} attribute {1} value is null", instance.getObjectName(), attName));
        }
        else {
            this.getLogger().fine(MessageFormat.format("MBean {0} attribute {1} is not a number ({2}/{3})", instance.getObjectName(), attName, value, value.getClass().getName()));
        }
    }
    
    private void process(final StatsEngine statsEngine) {
        final Set<String> metricNames = new HashSet<String>();
        final Collection<MBeanServer> srvrList = this.getServers();
        this.addNewFrameworks();
        this.runThroughAndRemoveInvokes(srvrList);
        for (final JmxGet object : this.jmxGets) {
            this.process(statsEngine, srvrList, object, metricNames);
        }
    }
    
    private Collection<MBeanServer> getServers() {
        Collection<MBeanServer> srvrList;
        if (this.alwaysIncludeMBeanServers.isEmpty() && this.toRemoveMBeanServers.isEmpty()) {
            srvrList = MBeanServerFactory.findMBeanServer(null);
        }
        else {
            srvrList = Sets.newHashSet(MBeanServerFactory.findMBeanServer(null));
            this.getLogger().log(Level.FINEST, "JMX Service : toRemove MBeansServers ({0})", new Object[] { this.toRemoveMBeanServers.size() });
            srvrList.removeAll(this.toRemoveMBeanServers);
            this.getLogger().log(Level.FINEST, "JMX Service : toAdd MBeansServers ({0})", new Object[] { this.alwaysIncludeMBeanServers.size() });
            srvrList.addAll(this.alwaysIncludeMBeanServers);
        }
        this.getLogger().log(Level.FINER, "JMX Service : querying MBeansServers ({0})", new Object[] { srvrList.size() });
        return srvrList;
    }
    
    private void addNewFrameworks() {
        for (JmxFrameworkValues framework = this.toBeAdded.poll(); framework != null; framework = this.toBeAdded.poll()) {
            this.jmxMetricFactory.convertFramework(framework, this.jmxGets, this.jmxInvokes);
        }
    }
    
    public void reloadExtensions(final Set<Extension> oldExtensions, final Set<Extension> extensions) {
        final Iterator<JmxGet> iterator = this.jmxGets.iterator();
        while (iterator.hasNext()) {
            if (oldExtensions.contains(iterator.next().getOrigin())) {
                iterator.remove();
            }
        }
        for (final Extension newExtension : extensions) {
            this.jmxMetricFactory.addExtension(newExtension, this.jmxGets);
        }
    }
}
