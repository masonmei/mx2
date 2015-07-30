// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.io.PrintStream;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;

abstract class AbstractTracingMethodAdapter extends AdviceAdapter
{
    private static final String JAVA_LANG_THROWABLE = "java/lang/Throwable";
    private static final boolean sDebugTracers = false;
    protected final String methodName;
    private int tracerLocalId;
    private final Label startFinallyLabel;
    protected final GenericClassAdapter genericClassAdapter;
    private int invocationHandlerIndex;
    protected final MethodBuilder methodBuilder;
    
    public AbstractTracingMethodAdapter(final GenericClassAdapter genericClassAdapter, final MethodVisitor mv, final int access, final Method method) {
        super(327680, mv, access, method.getName(), method.getDescriptor());
        this.startFinallyLabel = new Label();
        this.invocationHandlerIndex = -1;
        this.genericClassAdapter = genericClassAdapter;
        this.methodName = method.getName();
        this.methodBuilder = new MethodBuilder(this, access);
    }
    
    String getMethodDescriptor() {
        return this.methodDesc;
    }
    
    protected void systemOutPrint(final String message) {
        this.systemPrint(message, false);
    }
    
    protected void systemPrint(final String message, final boolean error) {
        this.getStatic(Type.getType(System.class), error ? "err" : "out", Type.getType(PrintStream.class));
        this.visitLdcInsn(message);
        this.invokeVirtual(Type.getType(PrintStream.class), new Method("println", "(Ljava/lang/String;)V"));
    }
    
    protected void onMethodEnter() {
        final int methodIndex = this.genericClassAdapter.addInstrumentedMethod(this);
        if (this.genericClassAdapter.canModifyClassStructure()) {
            this.setInvocationFieldIndex(methodIndex);
        }
        try {
            final Type tracerType = this.getTracerType();
            this.tracerLocalId = this.newLocal(tracerType);
            this.visitInsn(1);
            this.storeLocal(this.tracerLocalId);
            final Label startLabel = new Label();
            final Label endLabel = new Label();
            final Label exceptionLabel = new Label();
            this.mv.visitTryCatchBlock(startLabel, endLabel, exceptionLabel, "java/lang/Throwable");
            this.mv.visitLabel(startLabel);
            this.loadGetTracerArguments();
            this.invokeGetTracer();
            this.storeLocal(this.tracerLocalId);
            this.mv.visitLabel(endLabel);
            final Label doneLabel = new Label();
            this.goTo(doneLabel);
            this.mv.visitLabel(exceptionLabel);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                this.mv.visitMethodInsn(182, "java/lang/Throwable", "printStackTrace", "()V", false);
                this.systemPrint(MessageFormat.format("An error occurred creating a tracer for {0}.{1}{2}", this.genericClassAdapter.className, this.methodName, this.methodDesc), true);
            }
            else {
                final int exceptionVar = this.newLocal(Type.getType(Throwable.class));
                this.visitVarInsn(58, exceptionVar);
            }
            this.mv.visitLabel(doneLabel);
        }
        catch (Throwable e) {
            Agent.LOG.severe(MessageFormat.format("An error occurred transforming {0}.{1}{2} : {3}", this.genericClassAdapter.className, this.methodName, this.methodDesc, e.toString()));
            throw new RuntimeException(e);
        }
    }
    
    private void setInvocationFieldIndex(final int id) {
        this.invocationHandlerIndex = id;
    }
    
    public int getInvocationHandlerIndex() {
        return this.invocationHandlerIndex;
    }
    
    protected final Type getTracerType() {
        return MethodBuilder.INVOCATION_HANDLER_TYPE;
    }
    
    protected final void invokeGetTracer() {
        this.methodBuilder.invokeInvocationHandlerInterface(false);
    }
    
    protected abstract void loadGetTracerArguments();
    
    public GenericClassAdapter getGenericClassAdapter() {
        return this.genericClassAdapter;
    }
    
    public void visitCode() {
        super.visitCode();
        super.visitLabel(this.startFinallyLabel);
    }
    
    public void visitMaxs(final int maxStack, final int maxLocals) {
        final Label endFinallyLabel = new Label();
        super.visitTryCatchBlock(this.startFinallyLabel, endFinallyLabel, endFinallyLabel, "java/lang/Throwable");
        super.visitLabel(endFinallyLabel);
        this.onFinally(191);
        super.visitInsn(191);
        super.visitMaxs(maxStack, maxLocals);
    }
    
    protected void onMethodExit(final int opcode) {
        if (opcode != 191) {
            this.onFinally(opcode);
        }
    }
    
    protected void onFinally(final int opcode) {
        final Label end = new Label();
        if (opcode == 191) {
            if ("<init>".equals(this.methodName)) {
                return;
            }
            this.dup();
            final int exceptionVar = this.newLocal(Type.getType(Throwable.class));
            this.visitVarInsn(58, exceptionVar);
            this.loadLocal(this.tracerLocalId);
            this.ifNull(end);
            this.loadLocal(this.tracerLocalId);
            this.checkCast(MethodBuilder.INVOCATION_HANDLER_TYPE);
            this.invokeTraceFinishWithThrowable(exceptionVar);
        }
        else {
            Object loadReturnValue = null;
            if (opcode != 177) {
                loadReturnValue = new StoreReturnValueAndReload(opcode);
            }
            this.loadLocal(this.tracerLocalId);
            this.ifNull(end);
            this.loadLocal(this.tracerLocalId);
            this.invokeTraceFinish(opcode, loadReturnValue);
        }
        this.visitLabel(end);
    }
    
    protected final void invokeTraceFinish(final int opcode, final Object loadReturnValue) {
        this.methodBuilder.loadSuccessful().loadArray(Object.class, opcode, loadReturnValue).invokeInvocationHandlerInterface(true);
    }
    
    protected final void invokeTraceFinishWithThrowable(final int exceptionVar) {
        this.methodBuilder.loadUnsuccessful().loadArray(Object.class, new Runnable() {
            public void run() {
                AbstractTracingMethodAdapter.this.visitVarInsn(25, exceptionVar);
            }
        }).invokeInvocationHandlerInterface(true);
    }
    
    private final class StoreReturnValueAndReload implements Runnable
    {
        private final int returnVar;
        
        public StoreReturnValueAndReload(final int opcode) {
            Type returnType = Type.getReturnType(AbstractTracingMethodAdapter.this.methodDesc);
            if (returnType.getSize() == 2) {
                AbstractTracingMethodAdapter.this.dup2();
            }
            else {
                AbstractTracingMethodAdapter.this.dup();
            }
            returnType = AbstractTracingMethodAdapter.this.methodBuilder.box(returnType);
            AbstractTracingMethodAdapter.this.storeLocal(this.returnVar = AbstractTracingMethodAdapter.this.newLocal(returnType), returnType);
        }
        
        public void run() {
            AbstractTracingMethodAdapter.this.loadLocal(this.returnVar);
        }
    }
}
