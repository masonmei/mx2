// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.bridge.reflect.ClassReflection;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;

class ReflectionHelper
{
    private static final ReflectionHelper INSTANCE;
    private final Map<String, ClassReflector> classes;
    
    public ReflectionHelper() {
        this.classes = (Map<String, ClassReflector>)Maps.newHashMap();
        for (final java.lang.reflect.Method m : ClassReflection.class.getMethods()) {
            final Method staticMethod = Method.getMethod(m);
            if (m.getDeclaringClass().equals(ClassReflection.class) && staticMethod.getArgumentTypes().length > 0) {
                final Type targetClass = staticMethod.getArgumentTypes()[0];
                final Class<?>[] args = (Class<?>[])new Class[m.getParameterTypes().length - 1];
                System.arraycopy(m.getParameterTypes(), 1, args, 0, staticMethod.getArgumentTypes().length - 1);
                try {
                    m.getParameterTypes()[0].getMethod(m.getName(), args);
                    ClassReflector classReflector = this.classes.get(targetClass.getInternalName());
                    if (classReflector == null) {
                        classReflector = new ClassReflector();
                        this.classes.put(targetClass.getInternalName(), classReflector);
                    }
                    final Type[] argumentTypes = new Type[staticMethod.getArgumentTypes().length - 1];
                    System.arraycopy(staticMethod.getArgumentTypes(), 1, argumentTypes, 0, staticMethod.getArgumentTypes().length - 1);
                    final Method targetMethod = new Method(m.getName(), staticMethod.getReturnType(), argumentTypes);
                    classReflector.methods.put(targetMethod, staticMethod);
                }
                catch (NoSuchMethodException ex) {}
            }
        }
    }
    
    public static ReflectionHelper get() {
        return ReflectionHelper.INSTANCE;
    }
    
    public boolean process(final String owner, final String name, final String desc, final GeneratorAdapter generatorAdapter) {
        final ClassReflector classReflector = this.classes.get(owner);
        if (classReflector != null) {
            final Method method = classReflector.methods.get(new Method(name, desc));
            if (method != null) {
                generatorAdapter.invokeStatic(Type.getType(ClassReflection.class), method);
                return true;
            }
        }
        return false;
    }
    
    static {
        INSTANCE = new ReflectionHelper();
    }
    
    private static class ClassReflector
    {
        private final Map<Method, Method> methods;
        
        public ClassReflector() {
            this.methods = (Map<Method, Method>)Maps.newHashMap();
        }
    }
}
