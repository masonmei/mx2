// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import java.util.HashMap;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.instrumentation.pointcuts.MethodMapper;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class MethodMappersAdapter extends ClassVisitor
{
    private final Map<Method, java.lang.reflect.Method> methods;
    private final String className;
    private final String originalInterface;
    
    private MethodMappersAdapter(final ClassVisitor cv, final Map<Method, java.lang.reflect.Method> methods, final String originalInterface, final String className) {
        super(327680, cv);
        this.methods = methods;
        this.originalInterface = originalInterface;
        this.className = className;
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final Method originalMethod = new Method(name, desc);
        final java.lang.reflect.Method method = this.methods.remove(originalMethod);
        if (method != null) {
            this.addMethod(method, originalMethod);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    private void addMethod(final java.lang.reflect.Method method, final Method originalMethod) {
        final Method newMethod = InstrumentationUtils.getMethod(method);
        final MethodMapper methodMapper = method.getAnnotation(MethodMapper.class);
        final Type returnType = Type.getType(method.getReturnType());
        final GeneratorAdapter mv = new GeneratorAdapter(1, newMethod, null, null, this);
        mv.visitCode();
        mv.loadThis();
        for (int i = 0; i < newMethod.getArgumentTypes().length; ++i) {
            mv.loadArg(i);
        }
        if (methodMapper.invokeInterface()) {
            mv.invokeInterface(Type.getObjectType(this.originalInterface), originalMethod);
        }
        else {
            mv.invokeVirtual(Type.getObjectType(this.className), originalMethod);
        }
        mv.visitInsn(returnType.getOpcode(172));
        mv.visitMaxs(0, 0);
    }
    
    protected static Map<Method, java.lang.reflect.Method> getMethodMappers(final Class<?> type) {
        final Map<Method, java.lang.reflect.Method> methods = new HashMap<Method, java.lang.reflect.Method>();
        for (final java.lang.reflect.Method method : type.getDeclaredMethods()) {
            final MethodMapper annotation = method.getAnnotation(MethodMapper.class);
            if (annotation == null) {
                throw new RuntimeException("Method " + method.getName() + " does not have a MethodMapper annotation");
            }
            final String originalMethodName = annotation.originalMethodName();
            String orginalDescriptor = annotation.originalDescriptor();
            if ("".equals(orginalDescriptor)) {
                orginalDescriptor = InstrumentationUtils.getMethod(method).getDescriptor();
            }
            if (method.getName().equals(annotation.originalMethodName())) {
                final String msg = MessageFormat.format("Ignoring {0} method in {1}: method name is same as orginalMethodName", method.getName(), type.getClass().getName());
                Agent.LOG.fine(msg);
            }
            else {
                methods.put(new Method(originalMethodName, orginalDescriptor), method);
            }
        }
        return methods;
    }
    
    public static MethodMappersAdapter getMethodMappersAdapter(final ClassVisitor cv, final Map<Method, java.lang.reflect.Method> methods, final String originalInterface, final String className) {
        final Map<Method, java.lang.reflect.Method> methods2 = new HashMap<Method, java.lang.reflect.Method>(methods);
        return new MethodMappersAdapter(cv, methods2, originalInterface, className);
    }
    
    public static MethodMappersAdapter getMethodMappersAdapter(final ClassVisitor cv, final Class<?> type, final String className) {
        final Map<Method, java.lang.reflect.Method> methods = getMethodMappers(type);
        return new MethodMappersAdapter(cv, methods, type.getName(), className);
    }
}
