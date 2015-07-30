// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.netty;

import java.lang.reflect.Field;
import com.newrelic.agent.service.ServiceFactory;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.bridge.AgentBridge;
import java.net.InetSocketAddress;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class NettyPortPointCut extends PointCut implements EntryInvocationHandler
{
    private static final String POINT_CUT_NAME;
    private static final boolean DEFAULT_ENABLED = true;
    private static final String CLASS = "org/jboss/netty/bootstrap/ServerBootstrap";
    private static final String METHOD_NAME = "bind";
    private static final String METHOD_DESC = "(Ljava/net/SocketAddress;)Lorg/jboss/netty/channel/Channel;";
    
    public NettyPortPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(NettyPortPointCut.POINT_CUT_NAME, "netty_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("org/jboss/netty/bootstrap/ServerBootstrap");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("bind", "(Ljava/net/SocketAddress;)Lorg/jboss/netty/channel/Channel;");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        try {
            final InetSocketAddress socketAddress = (InetSocketAddress)args[0];
            AgentBridge.privateApi.setAppServerPort(socketAddress.getPort());
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINE, "Unable to get Netty port number", e);
        }
        final String playVersion = System.getProperty("play.version");
        if (playVersion != null) {
            ServiceFactory.getEnvironmentService().getEnvironment().setServerInfo("Play", playVersion);
        }
        else {
            String version;
            try {
                final Class<?> versionClass = object.getClass().getClassLoader().loadClass("org.jboss.netty.util.Version");
                final Field versionField = versionClass.getField("ID");
                version = (String)versionField.get(null);
            }
            catch (Throwable e2) {
                version = null;
            }
            ServiceFactory.getEnvironmentService().getEnvironment().setServerInfo("Netty", version);
        }
    }
    
    static {
        POINT_CUT_NAME = NettyPortPointCut.class.getName();
    }
}
