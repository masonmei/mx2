// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.ejb3;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.Iterator;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.util.asm.AnnotationDetails;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import java.io.IOException;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.util.asm.Utils;
import java.util.HashSet;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Set;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;

public class EJBAnnotationVisitor implements ClassMatchVisitorFactory
{
    private static final Set<String> EJB_DESCRIPTORS;
    private static final String EJB_REMOTE_INTERFCE_DESCRIPTOR;
    private static final String EJB_LOCAL_INTERFCE_DESCRIPTOR;
    private static final Object EJB_INTERFACE;
    
    public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
        return new ClassVisitor(327680, cv) {
            Set<Method> methodsToInstrument = new HashSet<Method>();
            
            public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                for (final String interfaceName : interfaces) {
                    if (interfaceName.equals(EJBAnnotationVisitor.EJB_INTERFACE)) {
                        try {
                            final ClassStructure classStructure = ClassStructure.getClassStructure(Utils.getClassResource(loader, interfaceName), 15);
                            this.collectMethodsToInstrument(classStructure);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                super.visit(version, access, name, signature, superName, interfaces);
            }
            
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (EJBAnnotationVisitor.EJB_DESCRIPTORS.contains(desc)) {
                    for (final String interfaceName : reader.getInterfaces()) {
                        try {
                            final ClassStructure classStructure = ClassStructure.getClassStructure(Utils.getClassResource(loader, interfaceName), 15);
                            final Map<String, AnnotationDetails> annotations = classStructure.getClassAnnotations();
                            if (annotations.containsKey(EJBAnnotationVisitor.EJB_REMOTE_INTERFCE_DESCRIPTOR) || annotations.containsKey(EJBAnnotationVisitor.EJB_LOCAL_INTERFCE_DESCRIPTOR)) {
                                this.collectMethodsToInstrument(classStructure);
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return super.visitAnnotation(desc, visible);
            }
            
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                final Method method = new Method(name, desc);
                if (this.methodsToInstrument.contains(method)) {
                    if (Agent.LOG.isFinerEnabled()) {
                        Agent.LOG.finer("Creating a tracer for " + reader.getClassName() + '.' + method);
                    }
                    context.addTrace(method, TraceDetailsBuilder.newBuilder().setInstrumentationType(InstrumentationType.BuiltIn).setInstrumentationSourceName(EJBAnnotationVisitor.class.getName()).build());
                }
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            
            private void collectMethodsToInstrument(final ClassStructure classStructure) {
                for (final Method m : classStructure.getMethods()) {
                    this.methodsToInstrument.add(m);
                }
            }
        };
    }
    
    static {
        EJB_DESCRIPTORS = ImmutableSet.of(Type.getObjectType("javax/ejb/Stateless").getDescriptor(), Type.getObjectType("javax/ejb/Stateful").getDescriptor());
        EJB_REMOTE_INTERFCE_DESCRIPTOR = Type.getObjectType("javax/ejb/Remote").getDescriptor();
        EJB_LOCAL_INTERFCE_DESCRIPTOR = Type.getObjectType("javax/ejb/Local").getDescriptor();
        EJB_INTERFACE = Type.getObjectType("javax/ejb/SessionBean");
    }
}
