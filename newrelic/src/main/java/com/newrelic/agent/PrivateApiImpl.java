// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.bridge.Agent;
import com.newrelic.agent.bridge.JmxApi;
import com.newrelic.agent.bridge.AsyncApi;
import com.newrelic.agent.errors.ErrorService;
import javax.management.MBeanServer;
import com.newrelic.agent.environment.Environment;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.jmx.JmxApiImpl;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Logger;
import com.newrelic.agent.attributes.AgentAttributeSender;
import com.newrelic.agent.attributes.AttributeSender;
import com.newrelic.agent.bridge.PrivateApi;

public class PrivateApiImpl implements PrivateApi
{
    private final AttributeSender attributeSender;
    
    public PrivateApiImpl() {
        this.attributeSender = new AgentAttributeSender();
    }
    
    public static void initialize(final Logger logger) {
        final PrivateApiImpl api = (PrivateApiImpl)(AgentBridge.privateApi = (PrivateApi)new PrivateApiImpl());
        AgentBridge.asyncApi = (AsyncApi)new AsyncApiImpl(logger);
        AgentBridge.jmxApi = (JmxApi)new JmxApiImpl();
        AgentBridge.agent = (Agent)new AgentImpl(logger);
    }
    
    public void setAppServerPort(final int port) {
        ServiceFactory.getEnvironmentService().getEnvironment().setServerPort(port);
    }
    
    public void setInstanceName(final String instanceName) {
        ServiceFactory.getEnvironmentService().getEnvironment().setInstanceName(instanceName);
    }
    
    public Closeable addSampler(final Runnable sampler, final int period, final TimeUnit timeUnit) {
        return ServiceFactory.getSamplerService().addSampler(sampler, period, timeUnit);
    }
    
    public void setServerInfo(final String dispatcherName, final String version) {
        ServiceFactory.getEnvironmentService().getEnvironment().setServerInfo(dispatcherName, version);
    }
    
    public void setServerInfo(final String serverInfo) {
        final Environment env = ServiceFactory.getEnvironmentService().getEnvironment();
        if (!env.getAgentIdentity().isServerInfoSet()) {
            env.setServerInfo(serverInfo);
        }
    }
    
    public void addMBeanServer(final MBeanServer server) {
        ServiceFactory.getJmxService().setJmxServer(server);
    }
    
    public void removeMBeanServer(final MBeanServer serverToRemove) {
        ServiceFactory.getJmxService().removeJmxServer(serverToRemove);
    }
    
    public void addCustomAttribute(final String key, final String value) {
        this.attributeSender.addAttribute(key, value, "addCustomAttribute");
    }
    
    public void addCustomAttribute(final String key, final Number value) {
        this.attributeSender.addAttribute(key, value, "addCustomAttribute");
    }
    
    public void addTracerParameter(final String key, final Number value) {
        if (Transaction.getTransaction().isInProgress()) {
            Transaction.getTransaction().getTransactionActivity().getLastTracer().setAttribute(key, value);
        }
    }
    
    public void reportHTTPError(final String message, final int statusCode, final String uri) {
        ErrorService.reportHTTPError(message, statusCode, uri);
    }
}
