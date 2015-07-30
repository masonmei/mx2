// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import com.newrelic.agent.stats.StatsWorks;
import com.newrelic.agent.stats.StatsService;
import com.newrelic.agent.service.ServiceManager;
import java.util.logging.Level;
import com.newrelic.bootstrap.BootstrapAgent;
import com.newrelic.agent.install.ConfigInstaller;
import com.newrelic.agent.service.ServiceManagerImpl;
import java.util.ResourceBundle;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelicApiImplementation;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.ConfigService;
import java.text.MessageFormat;
import com.newrelic.agent.logging.AgentLogManager;
import com.newrelic.agent.service.ServiceFactory;
import java.lang.instrument.Instrumentation;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.service.AbstractService;

public final class Agent extends AbstractService implements IAgent
{
    public static final IAgentLogger LOG;
    public static final int ASM_LEVEL = 327680;
    private static final String AGENT_ENABLED_PROPERTY = "newrelic.config.agent_enabled";
    private static final boolean DEBUG;
    private static final String VERSION;
    private static final ClassLoader NR_CLASS_LOADER;
    private static long agentPremainTime;
    private volatile boolean enabled;
    private final Instrumentation instrumentation;
    private volatile InstrumentationProxy instrumentationProxy;
    private static volatile boolean canFastPath;
    
    private Agent(final Instrumentation instrumentation) {
        super(IAgent.class.getSimpleName());
        this.enabled = true;
        this.instrumentation = instrumentation;
    }
    
    protected void doStart() {
        final ConfigService configService = ServiceFactory.getConfigService();
        final AgentConfig config = configService.getDefaultAgentConfig();
        AgentLogManager.configureLogger(config);
        this.logHostIp();
        Agent.LOG.info(MessageFormat.format("New Relic Agent v{0} is initializing...", getVersion()));
        if (!(this.enabled = config.isAgentEnabled())) {
            Agent.LOG.info("New Relic agent is disabled.");
        }
        this.instrumentationProxy = InstrumentationProxy.getInstrumentationProxy(this.instrumentation);
        this.initializeBridgeApis();
        final long startTime = System.currentTimeMillis();
        final Runnable runnable = new Runnable() {
            public void run() {
                Agent.this.jvmShutdown(startTime);
            }
        };
        final Thread shutdownThread = new Thread(runnable, "New Relic JVM Shutdown");
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }
    
    private void initializeBridgeApis() {
        NewRelicApiImplementation.initialize();
        PrivateApiImpl.initialize((Logger)Agent.LOG);
    }
    
    private void logHostIp() {
        try {
            final InetAddress address = InetAddress.getLocalHost();
            Agent.LOG.info("Agent Host: " + address.getHostName() + " IP: " + address.getHostAddress());
        }
        catch (UnknownHostException e) {
            Agent.LOG.info("New Relic could not identify host/ip.");
        }
    }
    
    protected void doStop() {
    }
    
    public void shutdownAsync() {
        final Runnable runnable = new Runnable() {
            public void run() {
                Agent.this.shutdown();
            }
        };
        final Thread shutdownThread = new Thread(runnable, "New Relic Shutdown");
        shutdownThread.start();
    }
    
    private void jvmShutdown(final long startTime) {
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        if (config.isSendDataOnExit() && System.currentTimeMillis() - startTime >= config.getSendDataOnExitThresholdInMillis()) {
            ServiceFactory.getHarvestService().harvestNow();
        }
        this.getLogger().info("JVM is shutting down");
        this.shutdown();
    }
    
    public synchronized void shutdown() {
        try {
            ServiceFactory.getServiceManager().stop();
            this.getLogger().info("New Relic Agent has shutdown");
        }
        catch (Exception e) {
            Agent.LOG.severe(MessageFormat.format("Error shutting down New Relic Agent: {0}", e));
        }
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public InstrumentationProxy getInstrumentation() {
        return this.instrumentationProxy;
    }
    
    public static String getVersion() {
        return Agent.VERSION;
    }
    
    public static ClassLoader getClassLoader() {
        return Agent.NR_CLASS_LOADER;
    }
    
    private static String initVersion() {
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(Agent.class.getName());
            return bundle.getString("version");
        }
        catch (Throwable t) {
            return "0.0";
        }
    }
    
    public static boolean isDebugEnabled() {
        return Agent.DEBUG;
    }
    
    public static boolean canFastPath() {
        return Agent.canFastPath;
    }
    
    public static void disableFastPath() {
        if (Agent.canFastPath) {
            Agent.canFastPath = false;
        }
    }
    
    public static void premain(final String agentArgs, final Instrumentation inst) {
        if (ServiceFactory.getServiceManager() != null) {
            Agent.LOG.warning("New Relic Agent is already running! Check if more than one -javaagent switch is used on the command line.");
            return;
        }
        final String enabled = System.getProperty("newrelic.config.agent_enabled");
        if (enabled != null && !Boolean.parseBoolean(enabled.toString())) {
            Agent.LOG.warning("New Relic agent is disabled by a system property.");
            return;
        }
        final String jvmName = System.getProperty("java.vm.name");
        if (jvmName.contains("Oracle JRockit")) {
            final String msg = MessageFormat.format("New Relic agent {0} does not support the Oracle JRockit JVM. Please use a 2.21.x or earlier version of the agent. JVM  is: {1}.", getVersion(), jvmName);
            Agent.LOG.error(msg);
        }
        try {
            final IAgent agent = new Agent(inst);
            final ServiceManager serviceManager = new ServiceManagerImpl(agent);
            ServiceFactory.setServiceManager(serviceManager);
            if (ConfigInstaller.isLicenseKeyEmpty(serviceManager.getConfigService().getDefaultAgentConfig().getLicenseKey())) {
                Agent.LOG.error("license_key is empty in the config. Not starting New Relic Agent.");
                return;
            }
            if (!serviceManager.getConfigService().getDefaultAgentConfig().isAgentEnabled()) {
                Agent.LOG.warning("agent_enabled is false in the config. Not starting New Relic Agent.");
                return;
            }
            serviceManager.start();
            Agent.LOG.info(MessageFormat.format("New Relic Agent v{0} has started", getVersion()));
            if (BootstrapAgent.isBootstrapClasspathFlagSet()) {
                Agent.LOG.info("The newrelic.bootstrap_classpath system property is deprecated.");
            }
            if (Agent.class.getClassLoader() == null) {
                Agent.LOG.info("Agent class loader is null which typically means the agent is loaded by the bootstrap class loader.");
            }
            else {
                Agent.LOG.info("Agent class loader: " + Agent.class.getClassLoader());
            }
            if (serviceManager.getConfigService().getDefaultAgentConfig().isStartupTimingEnabled()) {
                recordPremainTime(serviceManager.getStatsService());
            }
        }
        catch (Throwable t) {
            final String msg2 = MessageFormat.format("Unable to start New Relic agent: {0}", t);
            try {
                Agent.LOG.log(Level.SEVERE, msg2, t);
            }
            catch (Throwable t2) {}
            System.err.println(msg2);
            t.printStackTrace();
        }
    }
    
    public static void main(final String[] args) {
        final String javaVersion = System.getProperty("java.version");
        if (javaVersion.startsWith("1.5")) {
            final String msg = MessageFormat.format("Java version is: {0}.  This version of the New Relic Agent does not support Java 1.5.  Please use a 2.21.x or earlier version.", javaVersion);
            System.err.println("----------");
            System.err.println(msg);
            System.err.println("----------");
            return;
        }
        new AgentCommandLineParser().parseCommand(args);
    }
    
    public static long getAgentPremainTimeInMillis() {
        return Agent.agentPremainTime;
    }
    
    private static void recordPremainTime(final StatsService statsService) {
        Agent.agentPremainTime = System.currentTimeMillis() - BootstrapAgent.getAgentStartTime();
        Agent.LOG.log(Level.INFO, "Premain startup complete in {0}ms", new Object[] { Agent.agentPremainTime });
        statsService.doStatsWork(StatsWorks.getRecordResponseTimeWork("Supportability/Timing/Premain", Agent.agentPremainTime));
        final Map<String, Object> environmentInfo = ImmutableMap.<String, Object>builder()
                .put("Duration", Agent.agentPremainTime)
                .put("Version", getVersion())
                .put("JRE Vendor", System.getProperty("java.vendor"))
                .put("JRE Version", System.getProperty("java.version"))
                .put("JVM Vendor", System.getProperty("java.vm.vendor"))
                .put("JVM Version", System.getProperty("java.vm.version"))
                .put("JVM Runtime Version", System.getProperty("java.runtime.version"))
                .put("OS Name", System.getProperty("os.name"))
                .put("OS Version", System.getProperty("os.version"))
                .put("OS Arch", System.getProperty("os.arch"))
                .put("Processors", Runtime.getRuntime().availableProcessors())
                .put("Free Memory", Runtime.getRuntime().freeMemory())
                .put("Total Memory", Runtime.getRuntime().totalMemory())
                .put("Max Memory", Runtime.getRuntime().maxMemory())
                .build();
        Agent.LOG.log(Level.FINE, "Premain environment info: {0}", new Object[] { environmentInfo.toString() });
    }
    
    static {
        LOG = AgentLogManager.getLogger();
        DEBUG = Boolean.getBoolean("newrelic.debug");
        VERSION = initVersion();
        NR_CLASS_LOADER = ((BootstrapAgent.class.getClassLoader() == null) ? ClassLoader.getSystemClassLoader() : BootstrapAgent.class.getClassLoader());
        Agent.canFastPath = true;
    }
}
