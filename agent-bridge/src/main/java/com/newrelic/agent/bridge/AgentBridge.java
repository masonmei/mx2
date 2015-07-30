// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.MetricAggregator;
import java.lang.reflect.InvocationHandler;

public final class AgentBridge
{
    public static final Class<?>[] API_CLASSES;
    public static volatile PublicApi publicApi;
    public static volatile PrivateApi privateApi;
    public static volatile ObjectFieldManager objectFieldManager;
    public static volatile JmxApi jmxApi;
    public static volatile Instrumentation instrumentation;
    public static volatile AsyncApi asyncApi;
    public static volatile InvocationHandler agentHandler;
    public static volatile Agent agent;
    
    public static Agent getAgent() {
        return AgentBridge.agent;
    }
    
    static {
        API_CLASSES = new Class[] { PrivateApi.class, TracedMethod.class, Instrumentation.class, AsyncApi.class, Transaction.class, JmxApi.class, MetricAggregator.class };
        AgentBridge.publicApi = new NoOpPublicApi();
        AgentBridge.privateApi = new NoOpPrivateApi();
        AgentBridge.objectFieldManager = new NoOpObjectFieldManager();
        AgentBridge.jmxApi = new NoOpJmxApi();
        AgentBridge.instrumentation = new NoOpInstrumentation();
        AgentBridge.asyncApi = new NoOpAsyncApi();
        AgentBridge.agent = NoOpAgent.INSTANCE;
    }
}
