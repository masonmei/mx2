// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.util.Arrays;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import java.util.Iterator;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.ArrayList;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.util.Collection;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class GenericClassAdapter extends ClassVisitor
{
    private static final int MAX_VERSION = 51;
    private static final String CLINIT_METHOD_NAME = "<clinit>";
    private static final String NO_ARG_VOID_DESC = "()V";
    private static final String INIT_CLASS_METHOD_NAME = "__nr__initClass";
    protected final String className;
    private final ClassLoader classLoader;
    final Class<?> classBeingRedefined;
    private final List<AbstractTracingMethodAdapter> instrumentedMethods;
    private int version;
    private boolean processedClassInitMethod;
    private final Collection<PointCut> matches;
    private final InstrumentationContext context;
    
    public GenericClassAdapter(final ClassVisitor cv, final ClassLoader classLoader, final String className, final Class<?> classBeingRedefined, final Collection<PointCut> strongMatches, final InstrumentationContext context) {
        super(327680, cv);
        this.instrumentedMethods = new ArrayList<AbstractTracingMethodAdapter>();
        this.context = context;
        this.matches = strongMatches;
        this.classLoader = classLoader;
        this.className = className;
        this.classBeingRedefined = classBeingRedefined;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        final boolean isInterface = (access & 0x200) != 0x0;
        if (isInterface) {
            throw new StopProcessingException(name + " is an interface");
        }
        super.visit(version, access, name, signature, superName, interfaces);
        this.version = version;
    }
    
    boolean canModifyClassStructure() {
        return ClassTransformer.canModifyClassStructure(this.classLoader, this.classBeingRedefined);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (this.canModifyClassStructure() && "<clinit>".equals(name)) {
            mv = new InitMethodAdapter(mv, access, name, desc);
            this.processedClassInitMethod = true;
            return mv;
        }
        if ((access & 0x400) != 0x0) {
            return mv;
        }
        final PointCut pointCut = this.getMatch(access, name, desc);
        if (pointCut == null) {
            return mv;
        }
        final Method method = new Method(name, desc);
        this.context.addTimedMethods(method);
        if (this.canModifyClassStructure()) {
            this.context.addOldInvokerStyleInstrumentationMethod(method, pointCut);
            mv = new InvocationHandlerTracingMethodAdapter(this, mv, access, method);
        }
        else {
            final PointCutInvocationHandler pointCutInvocationHandler = pointCut.getPointCutInvocationHandler();
            final int id = ServiceFactory.getTracerService().getInvocationHandlerId(pointCutInvocationHandler);
            if (id == -1) {
                Agent.LOG.log(Level.FINE, "Unable to find invocation handler for method: {0} in class: {1}. Skipping instrumentation.", new Object[] { name, this.className });
            }
            else {
                this.context.addOldReflectionStyleInstrumentationMethod(method, pointCut);
                mv = new ReflectionStyleClassMethodAdapter(this, mv, access, method, id);
            }
        }
        return mv;
    }
    
    private PointCut getMatch(final int access, final String name, final String desc) {
        for (final PointCut pc : this.matches) {
            if (pc.getMethodMatcher().matches(-1, name, desc, MethodMatcher.UNSPECIFIED_ANNOTATIONS)) {
                return pc;
            }
        }
        return null;
    }
    
    public void visitEnd() {
        super.visitEnd();
        if ((this.canModifyClassStructure() && (this.processedClassInitMethod || this.instrumentedMethods.size() > 0)) || this.mustAddNRClassInit()) {
            this.createNRClassInitMethod();
        }
        if (this.instrumentedMethods.size() > 0) {
            if (this.canModifyClassStructure() || this.mustAddField()) {
                this.createInvocationHandlerField();
            }
            if ((this.canModifyClassStructure() || this.mustAddNRClassInit()) && !this.processedClassInitMethod) {
                this.createClassInitMethod();
            }
        }
    }
    
    private boolean mustAddNRClassInit() {
        if (this.classBeingRedefined == null) {
            return false;
        }
        try {
            this.classBeingRedefined.getDeclaredMethod("__nr__initClass", (Class<?>[])new Class[0]);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    private boolean mustAddField() {
        if (this.classBeingRedefined == null) {
            return false;
        }
        try {
            this.classBeingRedefined.getDeclaredField("__nr__InvocationHandlers");
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    private void createClassInitMethod() {
        MethodVisitor mv = this.cv.visitMethod(8, "<clinit>", "()V", null, null);
        mv = new InitMethodAdapter(mv, 8, "<clinit>", "()V");
        mv.visitCode();
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
    
    private void createNRClassInitMethod() {
        MethodVisitor mv = this.cv.visitMethod(8, "__nr__initClass", "()V", null, null);
        mv = new InitMethod(mv, 8, "__nr__initClass", "()V");
        mv.visitCode();
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
    
    private void createInvocationHandlerField() {
        this.cv.visitField(10, "__nr__InvocationHandlers", MethodBuilder.INVOCATION_HANDLER_ARRAY_TYPE.getDescriptor(), null, null);
    }
    
    int addInstrumentedMethod(final AbstractTracingMethodAdapter methodAdapter) {
        final int index = this.instrumentedMethods.size();
        this.instrumentedMethods.add(methodAdapter);
        return index;
    }
    
    public Collection<AbstractTracingMethodAdapter> getInstrumentedMethods() {
        return this.instrumentedMethods;
    }
    
    private class InitMethodAdapter extends AdviceAdapter
    {
        protected InitMethodAdapter(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(327680, mv, access, name, desc);
        }
        
        protected void onMethodEnter() {
            this.mv.visitMethodInsn(184, GenericClassAdapter.this.className, "__nr__initClass", "()V", false);
        }
    }
    
    private class InitMethod extends AdviceAdapter
    {
        private InitMethod(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(327680, mv, access, name, desc);
        }
        
        private int getAgentWrapper() {
            new MethodBuilder(this, this.methodAccess).loadInvocationHandlerFromProxy();
            final int invocationHandlerVar = this.newLocal(MethodBuilder.INVOCATION_HANDLER_TYPE);
            this.mv.visitVarInsn(58, invocationHandlerVar);
            return invocationHandlerVar;
        }
        
        protected void onMethodEnter() {
            if (GenericClassAdapter.this.classBeingRedefined != null) {
                return;
            }
            final int invocationHandlerVar = this.getAgentWrapper();
            this.visitLdcInsn(Type.getObjectType(GenericClassAdapter.this.className));
            final int classVar = this.newLocal(Type.getType(Object.class));
            this.mv.visitVarInsn(58, classVar);
            if (GenericClassAdapter.this.canModifyClassStructure()) {
                this.push(GenericClassAdapter.this.instrumentedMethods.size());
                this.newArray(MethodBuilder.INVOCATION_HANDLER_TYPE);
                this.putStatic(Type.getObjectType(GenericClassAdapter.this.className), "__nr__InvocationHandlers", MethodBuilder.INVOCATION_HANDLER_ARRAY_TYPE);
                for (final AbstractTracingMethodAdapter methodAdapter : GenericClassAdapter.this.instrumentedMethods) {
                    if (methodAdapter.getInvocationHandlerIndex() >= 0) {
                        this.initMethod(classVar, invocationHandlerVar, methodAdapter);
                    }
                }
            }
        }
        
        private void initMethod(final int classVar, final int invocationHandlerVar, final AbstractTracingMethodAdapter methodAdapter) {
            this.getStatic(Type.getObjectType(GenericClassAdapter.this.className), "__nr__InvocationHandlers", MethodBuilder.INVOCATION_HANDLER_ARRAY_TYPE);
            this.push(methodAdapter.getInvocationHandlerIndex());
            this.mv.visitVarInsn(25, invocationHandlerVar);
            this.mv.visitVarInsn(25, classVar);
            this.visitInsn(1);
            final List<Object> arguments = new ArrayList<Object>(Arrays.asList(GenericClassAdapter.this.className, methodAdapter.methodName, methodAdapter.getMethodDescriptor(), false, false));
            new MethodBuilder(this, this.methodAccess).loadArray(Object.class, arguments.toArray(new Object[arguments.size()])).invokeInvocationHandlerInterface(false);
            this.checkCast(MethodBuilder.INVOCATION_HANDLER_TYPE);
            this.arrayStore(MethodBuilder.INVOCATION_HANDLER_TYPE);
        }
    }
}
