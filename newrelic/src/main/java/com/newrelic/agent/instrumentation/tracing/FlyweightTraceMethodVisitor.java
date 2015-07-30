// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.base.Joiner;
import com.newrelic.agent.util.asm.Variables;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.Instrumentation;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.util.asm.BytecodeGenProxyBuilder;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;

public class FlyweightTraceMethodVisitor extends AdviceAdapter
{
    private static final Type JOINER_TYPE;
    final Map<Method, Handler> tracedMethodMethodHandlers;
    private final Method method;
    private final int startTimeLocal;
    private final Label startFinallyLabel;
    private final TraceDetails traceDetails;
    private final String className;
    private final int parentTracerLocal;
    private final int metricNameLocal;
    private final int rollupMetricNamesCacheId;
    static final Type THROWABLE_TYPE;
    
    public FlyweightTraceMethodVisitor(final String className, final MethodVisitor mv, final int access, final String name, final String desc, final TraceDetails trace, final Class<?> classBeingRedefined) {
        super(327680, mv, access, name, desc);
        this.className = className.replace('/', '.');
        this.method = new Method(name, desc);
        this.startFinallyLabel = new Label();
        this.startTimeLocal = this.newLocal(Type.LONG_TYPE);
        this.parentTracerLocal = this.newLocal(BridgeUtils.TRACED_METHOD_TYPE);
        this.metricNameLocal = this.newLocal(Type.getType(String.class));
        if (trace.rollupMetricName().length > 0) {
            this.rollupMetricNamesCacheId = AgentBridge.instrumentation.addToObjectCache((Object)trace.rollupMetricName());
        }
        else {
            this.rollupMetricNamesCacheId = -1;
        }
        this.traceDetails = trace;
        this.tracedMethodMethodHandlers = this.getTracedMethodMethodHandlers();
    }
    
    private Map<Method, Handler> getTracedMethodMethodHandlers() {
        final Map<Method, Handler> map = (Map<Method, Handler>)Maps.newHashMap();
        map.put(new Method("getMetricName", "()Ljava/lang/String;"), new Handler() {
            public void handle(final AdviceAdapter mv) {
                mv.loadLocal(FlyweightTraceMethodVisitor.this.metricNameLocal);
            }
        });
        map.put(new Method("setMetricName", "([Ljava/lang/String;)V"), new Handler() {
            public void handle(final AdviceAdapter mv) {
                mv.checkCast(Type.getType(Object[].class));
                FlyweightTraceMethodVisitor.this.push("");
                mv.invokeStatic(FlyweightTraceMethodVisitor.JOINER_TYPE, new Method("on", FlyweightTraceMethodVisitor.JOINER_TYPE, new Type[] { Type.getType(String.class) }));
                mv.swap();
                mv.invokeVirtual(FlyweightTraceMethodVisitor.JOINER_TYPE, new Method("join", Type.getType(String.class), new Type[] { Type.getType(Object[].class) }));
                mv.storeLocal(FlyweightTraceMethodVisitor.this.metricNameLocal);
            }
        });
        this.addUnsupportedMethod(map, new Method("nameTransaction", Type.VOID_TYPE, new Type[] { Type.getType(TransactionNamePriority.class) }));
        this.addUnsupportedMethod(map, new Method("setRollupMetricNames", "([Ljava/lang/String;)V"));
        this.addUnsupportedMethod(map, new Method("addRollupMetricName", "([Ljava/lang/String;)V"));
        this.addUnsupportedMethod(map, new Method("setMetricNameFormatInfo", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"));
        this.addUnsupportedMethod(map, new Method("addExclusiveRollupMetricName", "([Ljava/lang/String;)V"));
        map.put(new Method("getParentTracedMethod", "()Lcom/newrelic/agent/bridge/TracedMethod;"), new Handler() {
            public void handle(final AdviceAdapter mv) {
                mv.loadLocal(FlyweightTraceMethodVisitor.this.parentTracerLocal);
            }
        });
        return map;
    }
    
    private void addUnsupportedMethod(final Map<Method, Handler> map, final Method method) {
        map.put(method, new UnsupportedHandler(method));
    }
    
    protected void onMethodEnter() {
        super.onMethodEnter();
        this.push(0L);
        super.storeLocal(this.startTimeLocal, Type.LONG_TYPE);
        this.visitInsn(1);
        super.storeLocal(this.parentTracerLocal, BridgeUtils.TRACED_METHOD_TYPE);
        this.visitInsn(1);
        super.storeLocal(this.metricNameLocal);
        final Label start = new Label();
        final Label end = new Label();
        this.visitLabel(start);
        super.invokeStatic(Type.getType(System.class), new Method("nanoTime", Type.LONG_TYPE, new Type[0]));
        super.storeLocal(this.startTimeLocal, Type.LONG_TYPE);
        BridgeUtils.getCurrentTransaction(this);
        final Transaction transactionApi = BytecodeGenProxyBuilder.newBuilder(Transaction.class, this, true).build();
        transactionApi.startFlyweightTracer();
        super.storeLocal(this.parentTracerLocal, BridgeUtils.TRACED_METHOD_TYPE);
        String fullMetricName = this.traceDetails.getFullMetricName(this.className, this.method.getName());
        if (fullMetricName == null) {
            fullMetricName = Strings.join('/', "Java", this.className, this.method.getName());
        }
        this.push(fullMetricName);
        super.storeLocal(this.metricNameLocal);
        this.goTo(end);
        final Label handler = new Label();
        this.visitLabel(handler);
        this.pop();
        this.visitLabel(end);
        this.visitTryCatchBlock(start, end, handler, TraceMethodVisitor.THROWABLE_TYPE.getInternalName());
        super.visitLabel(this.startFinallyLabel);
    }
    
    public void visitMaxs(final int maxStack, final int maxLocals) {
        final Label endFinallyLabel = new Label();
        super.visitTryCatchBlock(this.startFinallyLabel, endFinallyLabel, endFinallyLabel, FlyweightTraceMethodVisitor.THROWABLE_TYPE.getInternalName());
        super.visitLabel(endFinallyLabel);
        this.onEveryExit(191);
        super.visitInsn(191);
        super.visitMaxs(maxStack, maxLocals);
    }
    
    protected void onMethodExit(final int opcode) {
        if (opcode != 191) {
            this.onEveryExit(opcode);
        }
    }
    
    protected void onEveryExit(final int opcode) {
        final Label skip = super.newLabel();
        super.loadLocal(this.parentTracerLocal);
        super.ifNull(skip);
        BridgeUtils.getCurrentTransaction(this);
        super.ifNull(skip);
        BridgeUtils.getCurrentTransaction(this);
        final BytecodeGenProxyBuilder<Transaction> builder = BytecodeGenProxyBuilder.newBuilder(Transaction.class, this, true);
        final Variables loader = builder.getVariables();
        String[] rollupMetricNames;
        if (this.rollupMetricNamesCacheId >= 0) {
            rollupMetricNames = loader.load(String[].class, (Runnable)new Runnable() {
                public void run() {
                    FlyweightTraceMethodVisitor.this.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "instrumentation", BridgeUtils.INSTRUMENTATION_TYPE);
                    BytecodeGenProxyBuilder.newBuilder(Instrumentation.class, FlyweightTraceMethodVisitor.this, true).build().getCachedObject(FlyweightTraceMethodVisitor.this.rollupMetricNamesCacheId);
                    FlyweightTraceMethodVisitor.this.checkCast(Type.getType(String[].class));
                }
            });
        }
        else {
            rollupMetricNames = null;
        }
        final long startTime = loader.loadLocal(this.startTimeLocal, Type.LONG_TYPE, -1L);
        final long loadEndTime = loader.load(Long.valueOf(-2L), new Runnable() {
            public void run() {
                FlyweightTraceMethodVisitor.this.invokeStatic(Type.getType(System.class), new Method("nanoTime", Type.LONG_TYPE, new Type[0]));
            }
        });
        final Transaction transactionApi = builder.build();
        transactionApi.finishFlyweightTracer((TracedMethod)loader.loadLocal(this.parentTracerLocal, TracedMethod.class), startTime, loadEndTime, this.className, this.method.getName(), this.methodDesc, (String)loader.loadLocal(this.metricNameLocal, String.class), rollupMetricNames);
        super.visitLabel(skip);
    }
    
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        if (!owner.equals(BridgeUtils.TRACED_METHOD_TYPE.getInternalName())) {
            super.visitFieldInsn(opcode, owner, name, desc);
        }
    }
    
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (BridgeUtils.isAgentType(owner) && "getTracedMethod".equals(name)) {
            this.pop();
        }
        else if (BridgeUtils.isTracedMethodType(owner)) {
            final Method method = new Method(name, desc);
            final Handler handler = this.tracedMethodMethodHandlers.get(method);
            if (handler != null) {
                handler.handle(this);
            }
        }
        else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
    
    static {
        JOINER_TYPE = Type.getType(Joiner.class);
        THROWABLE_TYPE = Type.getType(Throwable.class);
    }
    
    private static class UnsupportedHandler implements Handler
    {
        private final Method method;
        
        public UnsupportedHandler(final Method method) {
            this.method = method;
        }
        
        public void handle(final AdviceAdapter mv) {
            Agent.LOG.log(Level.FINER, "{0}.{1} is unsupported in flyweight tracers", new Object[] { TracedMethod.class.getSimpleName(), this.method });
        }
    }
    
    private interface Handler
    {
        void handle(AdviceAdapter p0);
    }
}
