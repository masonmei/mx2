// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.servlet;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Set;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;

public class ServletAnnotationVisitor implements ClassMatchVisitorFactory
{
    private static final String WEB_SERVLET_DESCRIPTOR;
    private static final Set<String> SERVLET_METHODS;
    
    public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, ClassVisitor cv, final InstrumentationContext context) {
        cv = new ClassVisitor(327680, cv) {
            private TraceDetails traceDetails;
            
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (this.traceDetails != null && ServletAnnotationVisitor.SERVLET_METHODS.contains(name)) {
                    context.addTrace(new Method(name, desc), this.traceDetails);
                }
                return mv;
            }
            
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (ServletAnnotationVisitor.WEB_SERVLET_DESCRIPTOR.equals(desc)) {
                    return new AnnotationVisitor(327680, super.visitAnnotation(desc, visible)) {
                        String[] urlPatterns;
                        
                        public AnnotationVisitor visitArray(final String name) {
                            AnnotationVisitor av = super.visitArray(name);
                            if ("value".equals(name) || "urlPatterns".equals(name)) {
                                av = new AnnotationVisitor(327680, av) {
                                    public void visit(final String name, final Object value) {
                                        super.visit(name, value);
                                        if (AnnotationVisitor.this.urlPatterns == null) {
                                            AnnotationVisitor.this.urlPatterns = new String[] { value.toString() };
                                        }
                                    }
                                };
                            }
                            return av;
                        }
                        
                        public void visitEnd() {
                            super.visitEnd();
                            if (this.urlPatterns != null) {
                                ClassVisitor.this.traceDetails = TraceDetailsBuilder.newBuilder().setInstrumentationType(InstrumentationType.BuiltIn).setInstrumentationSourceName(ServletAnnotationVisitor.class.getName()).setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "WebServletPath", this.urlPatterns[0]).build();
                            }
                        }
                    };
                }
                return super.visitAnnotation(desc, visible);
            }
        };
        return cv;
    }
    
    static {
        WEB_SERVLET_DESCRIPTOR = Type.getObjectType("javax/servlet/annotation/WebServlet").getDescriptor();
        SERVLET_METHODS = ImmutableSet.of("service", "doGet", "doPost", "doHead", "doPut", "doOptions", "doTrace");
    }
}
