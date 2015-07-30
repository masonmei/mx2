// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.environment;

import java.util.logging.Level;
import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.io.Serializable;
import java.util.Collection;
import java.io.Writer;
import com.newrelic.agent.Agent;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.lang.management.RuntimeMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.config.AgentConfig;
import java.util.List;
import java.util.regex.Pattern;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class Environment implements JSONStreamAware, Cloneable
{
    public static final String PHYSICAL_CORE_KEY = "Physical Processors";
    private static final String LOGICAL_CORE_KEY = "Logical Processors";
    private static final String TOTAL_MEMORY_MB = "Total Physical Memory (MB)";
    private static final String SOLR_VERSION_KEY = "Solr Version";
    private static final Pattern JSON_WORKAROUND;
    private final List<EnvironmentChangeListener> listeners;
    private final List<List<?>> environmentMap;
    private volatile AgentIdentity agentIdentity;
    private volatile Integer physicalCoreCount;
    private volatile Float physicalMemoryMB;
    private volatile Object solrVersion;
    
    public Environment(final AgentConfig config, final String logFilePath) {
        this.listeners = Lists.newCopyOnWriteArrayList();
        this.environmentMap = new ArrayList<List<?>>();
        if (config.isSendEnvironmentInfo()) {
            final OperatingSystemMXBean systemMXBean = ManagementFactory.getOperatingSystemMXBean();
            this.addVariable("Logical Processors", systemMXBean.getAvailableProcessors());
            this.addVariable("Arch", systemMXBean.getArch());
            this.addVariable("OS version", systemMXBean.getVersion());
            this.addVariable("OS", systemMXBean.getName());
            this.addVariable("Java vendor", System.getProperty("java.vendor"));
            this.addVariable("Java VM", System.getProperty("java.vm.name"));
            this.addVariable("Java VM version", System.getProperty("java.vm.version"));
            this.addVariable("Java version", System.getProperty("java.version"));
            this.addVariable("Log path", logFilePath);
            final MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            this.addVariable("Heap initial (MB)", heapMemoryUsage.getInit() / 1048576.0f);
            this.addVariable("Heap max (MB)", heapMemoryUsage.getMax() / 1048576.0f);
            if (config.isSendJvmProps()) {
                final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                final List<String> inputArguments = fixInputArguments(runtimeMXBean.getInputArguments());
                this.environmentMap.add(Arrays.asList("JVM arguments", inputArguments));
            }
        }
        String dispatcher = null;
        if (System.getProperty("com.sun.aas.installRoot") != null) {
            dispatcher = "Glassfish";
        }
        else if (System.getProperty("resin.home") != null) {
            dispatcher = "Resin";
        }
        else if (System.getProperty("org.apache.geronimo.base.dir") != null) {
            dispatcher = "Apache Geronimo";
        }
        else if (System.getProperty("weblogic.home") != null) {
            dispatcher = "WebLogic";
        }
        else if (System.getProperty("wlp.install.dir") != null) {
            dispatcher = "WebSphere Application Server";
        }
        else if (System.getProperty("was.install.root") != null) {
            dispatcher = "IBM WebSphere Application Server";
        }
        else if (System.getProperty("jboss.home") != null) {
            dispatcher = "JBoss";
        }
        else if (System.getProperty("jboss.home.dir") != null || System.getProperty("org.jboss.resolver.warning") != null || System.getProperty("jboss.partition.name") != null) {
            dispatcher = "JBoss Web";
        }
        else if (System.getProperty("catalina.home") != null) {
            dispatcher = "Apache Tomcat";
        }
        else if (System.getProperty("jetty.home") != null) {
            dispatcher = "Jetty";
        }
        this.addVariable("Framework", "java");
        final Number appServerPort = config.getProperty("appserver_port");
        Integer serverPort = null;
        if (appServerPort != null) {
            serverPort = appServerPort.intValue();
        }
        final String instanceName = config.getProperty("instance_name");
        this.agentIdentity = new AgentIdentity(dispatcher, null, serverPort, instanceName);
    }
    
    public void addEnvironmentChangeListener(final EnvironmentChangeListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeEnvironmentChangeListener(final EnvironmentChangeListener listener) {
        this.listeners.remove(listener);
    }
    
    private static List<String> fixInputArguments(final List<String> args) {
        final List<String> fixed = new ArrayList<String>(args.size());
        for (final String arg : args) {
            fixed.add(fixString(arg));
        }
        return fixed;
    }
    
    static String fixString(final String arg) {
        final Matcher matcher = Environment.JSON_WORKAROUND.matcher(arg);
        return matcher.replaceAll("");
    }
    
    public void setServerPort(final Integer port) {
        final AgentIdentity newIdentity = this.agentIdentity.createWithNewServerPort(port);
        if (newIdentity == null) {
            Agent.LOG.finest("Application server port already set, not changing it to port " + port);
        }
        else {
            Agent.LOG.finer("Application server port: " + port);
            this.agentIdentity = newIdentity;
            this.notifyListenersIdentityChanged();
        }
    }
    
    public void setInstanceName(final String instanceName) {
        final AgentIdentity newIdentity = this.agentIdentity.createWithNewInstanceName(instanceName);
        if (newIdentity == null) {
            Agent.LOG.finest("Instance Name already set, not changing it to " + instanceName);
        }
        else {
            Agent.LOG.finer("Application server instance name: " + instanceName);
            this.agentIdentity = newIdentity;
            this.notifyListenersIdentityChanged();
        }
    }
    
    private void notifyListenersIdentityChanged() {
        for (final EnvironmentChangeListener listener : this.listeners) {
            listener.agentIdentityChanged(this.agentIdentity);
        }
    }
    
    public AgentIdentity getAgentIdentity() {
        return this.agentIdentity;
    }
    
    public void addSolrVersion(final Object version) {
        if (this.solrVersion == null && version != null) {
            Agent.LOG.fine("Setting environment variable: Solr Version: " + version);
            this.solrVersion = version;
            this.notifyListenersIdentityChanged();
        }
        else if (version != null) {
            Agent.LOG.finest("Solr version already set, not changing it to version " + version);
        }
    }
    
    private void addVariable(final String name, final Object value) {
        this.environmentMap.add(Arrays.asList(name, value));
    }
    
    public Object getVariable(final String name) {
        for (final List<?> item : this.environmentMap) {
            if (name.equals(item.get(0))) {
                return item.get(1);
            }
        }
        return null;
    }
    
    public void writeJSONString(final Writer writer) throws IOException {
        final List<Object> map = new ArrayList<Object>(this.environmentMap);
        map.add(Arrays.asList("Dispatcher", this.agentIdentity.getDispatcher()));
        map.add(Arrays.asList("Physical Processors", this.physicalCoreCount));
        map.add(Arrays.asList("Total Physical Memory (MB)", this.physicalMemoryMB));
        if (this.agentIdentity.getDispatcherVersion() != null) {
            map.add(Arrays.asList("Dispatcher Version", this.agentIdentity.getDispatcherVersion()));
        }
        if (this.agentIdentity.getServerPort() != null) {
            map.add(Arrays.asList("Server port", this.agentIdentity.getServerPort()));
        }
        if (this.agentIdentity.getInstanceName() != null) {
            map.add(Arrays.asList("Instance Name", this.agentIdentity.getInstanceName()));
        }
        if (this.solrVersion != null) {
            map.add(Arrays.asList("Solr Version", this.solrVersion));
        }
        JSONArray.writeJSONString(map, writer);
    }
    
    public void setServerInfo(final String dispatcherName, final String version) {
        final AgentIdentity newIdentity = this.agentIdentity.createWithNewDispatcher(dispatcherName, version);
        if (newIdentity != null) {
            this.agentIdentity = newIdentity;
            Agent.LOG.log(Level.FINER, "The dispatcher was set to {0}:{1}.", new Object[] { dispatcherName, version });
            this.notifyListenersIdentityChanged();
        }
    }
    
    public void setServerInfo(final String serverInfo) {
        Agent.LOG.config("Server Info: " + serverInfo);
        final String[] info = serverInfo.split("/");
        if (info.length == 2) {
            this.setServerInfo(info[0], info[1]);
        }
    }
    
    static {
        JSON_WORKAROUND = Pattern.compile("\\\\+$");
    }
}
