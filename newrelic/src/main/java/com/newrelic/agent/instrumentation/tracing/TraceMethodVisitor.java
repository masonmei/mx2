// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.NoOpTracedMethod;
import com.newrelic.agent.bridge.ExitTracer;
import com.newrelic.agent.util.asm.Variables;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.util.asm.BytecodeGenProxyBuilder;
import com.newrelic.agent.bridge.Instrumentation;
import com.newrelic.agent.tracers.ClassMethodSignatures;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;

public class TraceMethodVisitor extends AdviceAdapter
{
    protected final Method method;
    private final int tracerLocal;
    private final Label startFinallyLabel;
    private final TraceDetails traceDetails;
    private final int access;
    private final boolean customTracer;
    protected final String className;
    private final int signatureId;
    static final Type TRACER_TYPE;
    static final Type THROWABLE_TYPE;
    public static final Method IGNORE_APDEX_METHOD;
    
    public TraceMethodVisitor(final String className, final MethodVisitor mv, final int access, final String name, final String desc, final TraceDetails trace, final boolean customTracer, final Class<?> classBeingRedefined) {
        super(327680, mv, access, name, desc);
        this.className = className.replace('/', '.');
        this.method = new Method(name, desc);
        this.access = access;
        this.customTracer = customTracer;
        this.startFinallyLabel = new Label();
        this.tracerLocal = this.newLocal(TraceMethodVisitor.TRACER_TYPE);
        this.traceDetails = trace;
        int signatureId = -1;
        final ClassMethodSignature signature = new ClassMethodSignature(this.className.intern(), this.method.getName().intern(), this.methodDesc.intern());
        if (classBeingRedefined != null) {
            signatureId = ClassMethodSignatures.get().getIndex(signature);
        }
        if (signatureId == -1) {
            signatureId = ClassMethodSignatures.get().add(signature);
        }
        this.signatureId = signatureId;
    }
    
    protected void onMethodEnter() {
        super.onMethodEnter();
        this.startTracer();
    }
    
    protected void startTracer() {
        this.visitInsn(1);
        this.storeLocal(this.tracerLocal, TraceMethodVisitor.TRACER_TYPE);
        this.visitLabel(this.startFinallyLabel);
        final Label start = new Label();
        final Label end = new Label();
        this.visitLabel(start);
        super.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "instrumentation", BridgeUtils.INSTRUMENTATION_TYPE);
        final String metricName = this.traceDetails.getFullMetricName(this.className, this.method.getName());
        final String tracerFactory = this.traceDetails.tracerFactoryName();
        final BytecodeGenProxyBuilder<Instrumentation> builder = BytecodeGenProxyBuilder.newBuilder(Instrumentation.class, this, true);
        final Variables loader = builder.getVariables();
        final Instrumentation instrumentation = builder.build();
        if (tracerFactory == null) {
            final int tracerFlags = this.getTracerFlags();
            instrumentation.createTracer(loader.loadThis(this.access), this.signatureId, metricName, tracerFlags);
        }
        else {
            final Object[] loadArgs = loader.load(Object[].class, (Runnable)new Runnable() {
                public void run() {
                    TraceMethodVisitor.this.loadArgArray();
                }
            });
            instrumentation.createTracer(loader.loadThis(this.access), this.signatureId, this.traceDetails.dispatcher(), metricName, tracerFactory, loadArgs);
        }
        this.storeLocal(this.tracerLocal, TraceMethodVisitor.TRACER_TYPE);
        this.goTo(end);
        final Label handler = new Label();
        this.visitLabel(handler);
        this.pop();
        this.visitLabel(end);
        this.visitTryCatchBlock(start, end, handler, TraceMethodVisitor.THROWABLE_TYPE.getInternalName());
    }
    
    private int getTracerFlags() {
        int tracerFlags = 2;
        if (this.traceDetails.dispatcher()) {
            tracerFlags |= 0x8;
        }
        if (this.traceDetails.isLeaf()) {
            tracerFlags |= 0x20;
        }
        if (!this.traceDetails.excludeFromTransactionTrace()) {
            tracerFlags |= 0x4;
        }
        if (this.customTracer) {
            tracerFlags |= 0x10;
        }
        return tracerFlags;
    }
    
    public void visitMaxs(final int maxStack, final int maxLocals) {
        final Label endFinallyLabel = new Label();
        super.visitTryCatchBlock(this.startFinallyLabel, endFinallyLabel, endFinallyLabel, TraceMethodVisitor.THROWABLE_TYPE.getInternalName());
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
        final Label isTracerNullLabel = new Label();
        this.loadLocal(this.tracerLocal);
        this.ifNull(isTracerNullLabel);
        if (191 == opcode) {
            this.dup();
        }
        this.loadLocal(this.tracerLocal);
        final ExitTracer tracer = BytecodeGenProxyBuilder.newBuilder(ExitTracer.class, this, false).build();
        if (191 == opcode) {
            this.swap();
            tracer.finish((Throwable)null);
        }
        else {
            this.push(opcode);
            this.visitInsn(1);
            tracer.finish(0, (Object)null);
        }
        this.visitLabel(isTracerNullLabel);
    }
    
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        if (owner.equals(BridgeUtils.TRACED_METHOD_TYPE.getInternalName())) {
            this.loadTracer();
        }
        else {
            super.visitFieldInsn(opcode, owner, name, desc);
        }
    }
    
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (BridgeUtils.isAgentType(owner) && "getTracedMethod".equals(name)) {
            this.pop();
            this.loadTracer();
        }
        else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
    
    private void loadTracer() {
        final Label isTracerNullLabel = new Label();
        final Label end = new Label();
        this.loadLocal(this.tracerLocal);
        this.ifNull(isTracerNullLabel);
        this.loadLocal(this.tracerLocal);
        this.goTo(end);
        this.visitLabel(isTracerNullLabel);
        this.getStatic(Type.getType(NoOpTracedMethod.class), "INSTANCE", Type.getType(TracedMethod.class));
        this.visitLabel(end);
    }
    
    static {
        TRACER_TYPE = Type.getType(ExitTracer.class);
        THROWABLE_TYPE = Type.getType(Throwable.class);
        IGNORE_APDEX_METHOD = new Method("ignoreApdex", Type.VOID_TYPE, new Type[0]);
    }
}
