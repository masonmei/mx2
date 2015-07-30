// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.utilization;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.Hostname;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import com.newrelic.agent.service.AbstractService;

public class UtilizationService extends AbstractService
{
    public static final String DETECT_AWS_KEY = "utilization.detect_aws";
    public static final String DETECT_DOCKER_KEY = "utilization.detect_docker";
    private volatile UtilizationData utilizationData;
    private final String hostName;
    private Future<UtilizationData> future;
    private static final String THREAD_NAME = "New Relic Utilization Service";
    private static final ExecutorService executor;
    private final boolean isLinux;
    private final boolean detectAws;
    private final boolean detectDocker;
    private static final AWS aws;
    
    public UtilizationService() {
        super(UtilizationService.class.getSimpleName());
        this.utilizationData = UtilizationData.EMPTY;
        this.future = null;
        final AgentConfig agentConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.detectAws = (Boolean)config.getValue("utilization.detect_aws", (Object)Boolean.TRUE);
        this.detectDocker = (Boolean)config.getValue("utilization.detect_docker", (Object)Boolean.TRUE);
        this.hostName = Hostname.getHostname(Agent.LOG, agentConfig);
        this.isLinux = isLinuxOs();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() throws Exception {
        this.scheduleUtilizationTask();
    }
    
    protected void doStop() throws Exception {
        UtilizationService.executor.shutdownNow();
    }
    
    private void scheduleUtilizationTask() {
        this.future = UtilizationService.executor.submit(new UtilizationTask());
    }
    
    public UtilizationData updateUtilizationData() {
        if (this.future == null) {
            this.future = UtilizationService.executor.submit(new UtilizationTask());
        }
        try {
            this.utilizationData = this.future.get(1000L, TimeUnit.MILLISECONDS);
            this.future = null;
        }
        catch (InterruptedException e) {
            this.cleanupAndLogThrowable(e);
        }
        catch (ExecutionException e2) {
            this.cleanupAndLogThrowable(e2);
        }
        catch (TimeoutException e3) {
            this.cleanupAndlogTimeout();
        }
        catch (Throwable t) {
            this.cleanupAndLogThrowable(t);
        }
        return this.utilizationData;
    }
    
    private void cleanupAndlogTimeout() {
        MemoryData.cleanup();
        Agent.LOG.log(Level.FINER, "Utilization task timed out. Returning cached utilization data.");
    }
    
    private void cleanupAndLogThrowable(final Throwable t) {
        MemoryData.cleanup();
        Agent.LOG.log(Level.FINEST, MessageFormat.format("Utilization task exception. Returning cached utilization data. {0}", t));
    }
    
    private static boolean isLinuxOs() {
        final String os = ManagementFactory.getOperatingSystemMXBean().getName();
        final boolean outcome = os != null && !os.startsWith("Windows") && !os.startsWith("Mac");
        Agent.LOG.log(Level.FINEST, "Docker info is {1} gathered because OS is {0}.", new Object[] { os, outcome ? "" : "not" });
        return outcome;
    }
    
    protected AWS.AwsData getAwsData() {
        return UtilizationService.aws.getAwsData();
    }
    
    protected String getDockerContainerId() {
        return DockerData.getDockerContainerId(this.isLinux);
    }
    
    static {
        executor = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("New Relic Utilization Service", true));
        aws = new AWS();
    }
    
    class UtilizationTask implements Callable<UtilizationData>
    {
        public UtilizationData call() throws Exception {
            return this.doUpdateUtilizationData();
        }
        
        private UtilizationData doUpdateUtilizationData() {
            final int processorCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
            final String containerId = UtilizationService.this.detectDocker ? UtilizationService.this.getDockerContainerId() : null;
            final AWS.AwsData awsData = UtilizationService.this.detectAws ? UtilizationService.this.getAwsData() : AWS.AwsData.EMPTY_DATA;
            final long total_ram_mib = MemoryData.getTotalRamInMib();
            return new UtilizationData(UtilizationService.this.hostName, processorCount, containerId, awsData, total_ram_mib);
        }
    }
}
