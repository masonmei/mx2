// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.tracing.Annotation;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.api.agent.Trace;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.config.ClassTransformerConfig;
import com.newrelic.agent.config.ConfigService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.annotationmatchers.AnnotationMatcher;

class TraceMatchVisitor implements ClassMatchVisitorFactory
{
    private final AnnotationMatcher traceAnnotationMatcher;
    private final AnnotationMatcher ignoreTransactionAnnotationMatcher;
    private final AnnotationMatcher ignoreApdexAnnotationMatcher;
    
    public TraceMatchVisitor() {
        final ConfigService configService = ServiceFactory.getConfigService();
        final ClassTransformerConfig classTransformerConfig = configService.getDefaultAgentConfig().getClassTransformerConfig();
        this.traceAnnotationMatcher = classTransformerConfig.getTraceAnnotationMatcher();
        this.ignoreTransactionAnnotationMatcher = classTransformerConfig.getIgnoreTransactionAnnotationMatcher();
        this.ignoreApdexAnnotationMatcher = classTransformerConfig.getIgnoreApdexAnnotationMatcher();
    }
    
    public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
        return new ClassVisitor(327680, cv) {
            String source;
            
            public void visitSource(final String source, final String debug) {
                super.visitSource(source, debug);
                this.source = source;
            }
            
            public MethodVisitor visitMethod(final int access, final String methodName, final String methodDesc, final String signature, final String[] exceptions) {
                return new MethodVisitor(327680, super.visitMethod(access, methodName, methodDesc, signature, exceptions)) {
                    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                        if (TraceMatchVisitor.this.traceAnnotationMatcher.matches(desc)) {
                            final Annotation node = new Annotation(super.visitAnnotation(desc, visible), Type.getDescriptor(Trace.class), TraceDetailsBuilder.newBuilder().setInstrumentationType(InstrumentationType.TraceAnnotation).setInstrumentationSourceName(ClassVisitor.this.source)) {
                                public void visitEnd() {
                                    context.putTraceAnnotation(new Method(methodName, methodDesc), this.getTraceDetails(true));
                                    super.visitEnd();
                                }
                            };
                            return node;
                        }
                        if (TraceMatchVisitor.this.ignoreApdexAnnotationMatcher.matches(desc)) {
                            context.addIgnoreApdexMethod(methodName, methodDesc);
                        }
                        if (TraceMatchVisitor.this.ignoreTransactionAnnotationMatcher.matches(desc)) {
                            context.addIgnoreTransactionMethod(methodName, methodDesc);
                        }
                        return super.visitAnnotation(desc, visible);
                    }
                };
            }
        };
    }
}
