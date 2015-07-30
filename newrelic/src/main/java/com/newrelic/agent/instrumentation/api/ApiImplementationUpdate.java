// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.api;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import java.security.ProtectionDomain;
import java.util.Set;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMultimap;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;

public class ApiImplementationUpdate implements ContextClassTransformer
{
    private final DefaultApiImplementations defaultImplementations;
    private final ClassMatchVisitorFactory matcher;
    
    public static void setup(final InstrumentationContextManager manager) throws Exception {
        final ApiImplementationUpdate transformer = new ApiImplementationUpdate();
        manager.addContextClassTransformer(transformer.matcher, transformer);
    }
    
    protected ApiImplementationUpdate() throws Exception {
        this.defaultImplementations = new DefaultApiImplementations();
        this.matcher = new ClassMatchVisitorFactory() {
            public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, ClassVisitor cv, final InstrumentationContext context) {
                for (final String name : reader.getInterfaces()) {
                    final Map<Method, MethodNode> unmodifiableMethods = ApiImplementationUpdate.this.defaultImplementations.getApiClassNameToDefaultMethods().get(name);
                    if (unmodifiableMethods != null) {
                        final Map<Method, MethodNode> methods = (Map<Method, MethodNode>)Maps.newHashMap((Map<?, ?>)unmodifiableMethods);
                        cv = new ClassVisitor(327680, cv) {
                            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                                methods.remove(new Method(name, desc));
                                return super.visitMethod(access, name, desc, signature, exceptions);
                            }
                            
                            public void visitEnd() {
                                if (!methods.isEmpty()) {
                                    context.putMatch(ApiImplementationUpdate.this.matcher, new OptimizedClassMatcher.Match((Multimap<ClassAndMethodMatcher, String>)ImmutableMultimap.of(), methods.keySet(), null));
                                }
                                super.visitEnd();
                            }
                        };
                    }
                }
                return cv;
            }
        };
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
        final ClassReader reader = new ClassReader(classfileBuffer);
        ClassVisitor cv;
        final ClassWriter writer = (ClassWriter)(cv = new ClassWriter(1));
        for (final String name : reader.getInterfaces()) {
            final Map<Method, MethodNode> methods = this.defaultImplementations.getApiClassNameToDefaultMethods().get(name);
            if (methods != null) {
                final Map<Method, MethodNode> methodsToAdd = (Map<Method, MethodNode>)Maps.newHashMap((Map<?, ?>)methods);
                cv = new ClassVisitor(327680, cv) {
                    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                        methodsToAdd.remove(new Method(name, desc));
                        return super.visitMethod(access, name, desc, signature, exceptions);
                    }
                    
                    public void visitEnd() {
                        if (!methodsToAdd.isEmpty()) {
                            final Map<Method, MethodNode> missingMethods = (Map<Method, MethodNode>)Maps.newHashMap((Map<?, ?>)methodsToAdd);
                            for (final Map.Entry<Method, MethodNode> entry : missingMethods.entrySet()) {
                                entry.getValue().accept(this);
                            }
                        }
                        super.visitEnd();
                    }
                };
            }
        }
        reader.accept(cv, 8);
        return writer.toByteArray();
    }
    
    protected ClassMatchVisitorFactory getMatcher() {
        return this.matcher;
    }
}
