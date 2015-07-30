// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.agent.bridge.Instrumentation;
import com.newrelic.api.agent.Agent;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.PublicApi;
import com.newrelic.agent.bridge.PrivateApi;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.util.asm.VariableLoader;
import java.util.logging.Level;
import com.newrelic.api.agent.Logger;
import com.newrelic.agent.util.asm.BytecodeGenProxyBuilder;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

public class BridgeUtils
{
    public static final Type NEW_RELIC_API_TYPE;
    public static final Type PRIVATE_API_TYPE;
    public static final Type PUBLIC_API_TYPE;
    public static final Type AGENT_BRIDGE_TYPE;
    public static final Type TRACED_METHOD_TYPE;
    public static final Type PUBLIC_AGENT_TYPE;
    public static final Type INTERNAL_AGENT_TYPE;
    public static final Type INSTRUMENTATION_TYPE;
    public static final Type TRANSACTION_TYPE;
    public static final String PRIVATE_API_FIELD_NAME = "privateApi";
    public static final String PUBLIC_API_FIELD_NAME = "publicApi";
    public static final String INSTRUMENTATION_FIELD_NAME = "instrumentation";
    public static final String GET_TRACED_METHOD_METHOD_NAME = "getTracedMethod";
    public static final String GET_TRANSACTION_METHOD_NAME = "getTransaction";
    private static final String AGENT_FIELD_NAME = "agent";
    private static final String GET_LOGGER_METHOD_NAME = "getLogger";
    private static final Type LOGGER_TYPE;
    public static final Type WEAVER_TYPE;
    public static final String CURRENT_TRANSACTION_FIELD_NAME = "CURRENT";
    private static final Set<String> AGENT_CLASS_NAMES;
    private static final Set<String> TRACED_METHOD_CLASS_NAMES;
    private static final Set<String> TRANSACTION_CLASS_NAMES;
    
    public static void loadLogger(final GeneratorAdapter mv) {
        mv.visitFieldInsn(178, BridgeUtils.AGENT_BRIDGE_TYPE.getInternalName(), "agent", BridgeUtils.INTERNAL_AGENT_TYPE.getDescriptor());
        mv.invokeInterface(BridgeUtils.PUBLIC_AGENT_TYPE, new Method("getLogger", BridgeUtils.LOGGER_TYPE, new Type[0]));
    }
    
    public static BytecodeGenProxyBuilder<Logger> getLoggerBuilder(final GeneratorAdapter mv, final boolean loadArgs) {
        final BytecodeGenProxyBuilder<Logger> builder = BytecodeGenProxyBuilder.newBuilder(Logger.class, mv, loadArgs);
        if (loadArgs) {
            builder.addLoader(Type.getType(Level.class), new VariableLoader() {
                public void load(final Object value, final GeneratorAdapter methodVisitor) {
                    methodVisitor.getStatic(Type.getType(Level.class), ((Level)value).getName(), Type.getType(Level.class));
                }
            });
        }
        return builder;
    }
    
    public static Logger getLogger(final GeneratorAdapter mv) {
        loadLogger(mv);
        return getLoggerBuilder(mv, true).build();
    }
    
    public static void getCurrentTransaction(final MethodVisitor mv) {
        mv.visitFieldInsn(178, BridgeUtils.TRANSACTION_TYPE.getInternalName(), "CURRENT", BridgeUtils.TRANSACTION_TYPE.getDescriptor());
    }
    
    public static boolean isAgentType(final String owner) {
        return BridgeUtils.AGENT_CLASS_NAMES.contains(owner);
    }
    
    public static boolean isTracedMethodType(final String owner) {
        return BridgeUtils.TRACED_METHOD_CLASS_NAMES.contains(owner);
    }
    
    public static boolean isTransactionType(final String owner) {
        return BridgeUtils.TRANSACTION_CLASS_NAMES.contains(owner);
    }
    
    static {
        NEW_RELIC_API_TYPE = Type.getType(NewRelic.class);
        PRIVATE_API_TYPE = Type.getType(PrivateApi.class);
        PUBLIC_API_TYPE = Type.getType(PublicApi.class);
        AGENT_BRIDGE_TYPE = Type.getType(AgentBridge.class);
        TRACED_METHOD_TYPE = Type.getType(TracedMethod.class);
        PUBLIC_AGENT_TYPE = Type.getType(Agent.class);
        INTERNAL_AGENT_TYPE = Type.getType(com.newrelic.agent.bridge.Agent.class);
        INSTRUMENTATION_TYPE = Type.getType(Instrumentation.class);
        TRANSACTION_TYPE = Type.getType(Transaction.class);
        LOGGER_TYPE = Type.getType(Logger.class);
        WEAVER_TYPE = Type.getType(Weaver.class);
        AGENT_CLASS_NAMES = ImmutableSet.of(BridgeUtils.PUBLIC_AGENT_TYPE.getInternalName(), BridgeUtils.INTERNAL_AGENT_TYPE.getInternalName());
        TRACED_METHOD_CLASS_NAMES = ImmutableSet.of(BridgeUtils.TRACED_METHOD_TYPE.getInternalName(), Type.getInternalName(com.newrelic.api.agent.TracedMethod.class));
        TRANSACTION_CLASS_NAMES = ImmutableSet.of(BridgeUtils.TRANSACTION_TYPE.getInternalName(), Type.getInternalName(com.newrelic.api.agent.Transaction.class), Type.getInternalName(com.newrelic.agent.Transaction.class));
    }
}
