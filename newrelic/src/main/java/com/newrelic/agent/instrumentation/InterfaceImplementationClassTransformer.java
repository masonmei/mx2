// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.HashSet;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import com.newrelic.agent.util.Annotations;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;

public class InterfaceImplementationClassTransformer extends AbstractImplementationClassTransformer
{
    private final Map<Method, java.lang.reflect.Method> methods;
    private final boolean genericInterfaceSupportEnabled;
    
    public static StartableClassFileTransformer[] getClassTransformers(final ClassTransformer classTransformer) {
        final Collection<Class<?>> interfaces = Annotations.getAnnotationClassesFromManifest(InterfaceMapper.class, "com/newrelic/agent/instrumentation/pointcuts");
        final List<StartableClassFileTransformer> transformers = new ArrayList<StartableClassFileTransformer>(interfaces.size());
        for (final Class interfaceClass : interfaces) {
            transformers.add(new InterfaceImplementationClassTransformer(classTransformer, true, interfaceClass));
        }
        return transformers.toArray(new StartableClassFileTransformer[0]);
    }
    
    public InterfaceImplementationClassTransformer(final ClassTransformer classTransformer, final boolean enabled, final Class interfaceToImplement) {
        super(classTransformer, enabled, interfaceToImplement);
        boolean genericInterfaceSupportEnabled = true;
        Map<Method, java.lang.reflect.Method> methods2 = Collections.emptyMap();
        final InterfaceMapper mapper = interfaceToImplement.getAnnotation(InterfaceMapper.class);
        Class<?> visitorClass = mapper.classVisitor();
        if (visitorClass == Object.class) {
            visitorClass = InterfaceImplementationClassVisitor.class;
        }
        if (visitorClass == InterfaceImplementationClassVisitor.class) {
            genericInterfaceSupportEnabled = false;
            methods2 = MethodMappersAdapter.getMethodMappers(interfaceToImplement);
        }
        this.methods = Collections.unmodifiableMap((Map<? extends Method, ? extends java.lang.reflect.Method>)methods2);
        this.genericInterfaceSupportEnabled = (genericInterfaceSupportEnabled && mapper.className().length == 0);
    }
    
    protected boolean isGenericInterfaceSupportEnabled() {
        return this.genericInterfaceSupportEnabled;
    }
    
    protected ClassVisitor createClassVisitor(final ClassReader cr, final ClassWriter cw, final String className, final ClassLoader loader) {
        final InterfaceMapper mapper = this.interfaceToImplement.getAnnotation(InterfaceMapper.class);
        final Set<Method> methods2 = new HashSet<Method>(this.methods.keySet());
        Class<?> classVisitorClass = mapper.classVisitor();
        if (classVisitorClass == Object.class) {
            classVisitorClass = InterfaceImplementationClassVisitor.class;
        }
        if (InterfaceImplementationClassVisitor.class == classVisitorClass) {
            ClassVisitor classVisitor = new AddInterfaceAdapter(cw, className, this.interfaceToImplement);
            classVisitor = RequireMethodsAdapter.getRequireMethodsAdaptor(classVisitor, methods2, className, this.interfaceToImplement.getName(), loader);
            classVisitor = MethodMappersAdapter.getMethodMappersAdapter(classVisitor, this.methods, this.originalInterface, className);
            return classVisitor;
        }
        if (ClassVisitor.class.isAssignableFrom(mapper.classVisitor())) {
            try {
                final Constructor constructor = mapper.classVisitor().getConstructor(ClassVisitor.class, String.class);
                return constructor.newInstance(cw, className);
            }
            catch (Throwable e) {
                Agent.LOG.log(Level.FINEST, "while creating ClassVisitor for InterfaceMapper transformation", e);
            }
        }
        Agent.LOG.log(Level.FINEST, "Unable to create ClassVisitor (type {0}) for {1} with loader {2}", new Object[] { classVisitorClass, className, loader });
        return cw;
    }
    
    public class InterfaceImplementationClassVisitor extends ClassVisitor
    {
        public InterfaceImplementationClassVisitor(final int api) {
            super(api);
        }
    }
}
