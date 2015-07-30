// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.spring;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.instrumentation.context.TraceDetailsList;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import java.util.Set;

public class SpringAnnotationVisitor
{
    private static final Set<String> CONTROLLER_DESCRIPTORS;
    private static final String REQUEST_MAPPING_DESCRIPTOR;
    
    public final ClassMatchVisitorFactory getClassMatchVisitorFactory() {
        return new ClassMatchVisitorFactory() {
            public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
                return SpringAnnotationVisitor.createClassVisitor(reader.getClassName(), cv, context);
            }
        };
    }
    
    public static ClassVisitor createClassVisitor(final String internalClassName, final ClassVisitor cv, final TraceDetailsList context) {
        return new ClassVisitor(327680, cv) {
            private boolean isController = false;
            private String rootPath;
            
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                AnnotationVisitor av = super.visitAnnotation(desc, visible);
                if (SpringAnnotationVisitor.CONTROLLER_DESCRIPTORS.contains(desc)) {
                    this.isController = true;
                }
                if (SpringAnnotationVisitor.REQUEST_MAPPING_DESCRIPTOR.equals(desc)) {
                    av = new AnnotationVisitor(327680, av) {
                        public AnnotationVisitor visitArray(final String name) {
                            final AnnotationVisitor av = super.visitArray(name);
                            if ("value".equals(name)) {
                                return new AnnotationVisitor(327680, av) {
                                    public void visit(final String name, final Object value) {
                                        super.visit(name, value);
                                        ClassVisitor.this.rootPath = value.toString();
                                    }
                                };
                            }
                            return av;
                        }
                    };
                }
                return av;
            }
            
            public MethodVisitor visitMethod(final int access, final String methodName, final String methodDesc, final String signature, final String[] exceptions) {
                if (!this.isController) {
                    return super.visitMethod(access, methodName, methodDesc, signature, exceptions);
                }
                return new MethodVisitor(327680, super.visitMethod(access, methodName, methodDesc, signature, exceptions)) {
                    String path;
                    String httpMethod = "GET";
                    
                    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                        if (SpringAnnotationVisitor.REQUEST_MAPPING_DESCRIPTOR.equals(desc)) {
                            return new AnnotationVisitor(327680, super.visitAnnotation(desc, visible)) {
                                public AnnotationVisitor visitArray(final String name) {
                                    final AnnotationVisitor av = super.visitArray(name);
                                    if ("value".equals(name)) {
                                        return new AnnotationVisitor(327680, av) {
                                            public void visit(final String name, final Object value) {
                                                super.visit(name, value);
                                                MethodVisitor.this.path = value.toString();
                                            }
                                        };
                                    }
                                    if ("method".equals(name)) {
                                        return new AnnotationVisitor(327680, av) {
                                            public void visitEnum(final String name, final String desc, final String value) {
                                                super.visitEnum(name, desc, value);
                                                MethodVisitor.this.httpMethod = value;
                                            }
                                        };
                                    }
                                    return av;
                                }
                                
                                public void visitEnd() {
                                    super.visitEnd();
                                    if (MethodVisitor.this.path == null && ClassVisitor.this.rootPath == null) {
                                        Agent.LOG.log(Level.FINE, "No path was specified for SpringController {0}", new Object[] { internalClassName });
                                    }
                                    else {
                                        final TraceDetailsBuilder builder = TraceDetailsBuilder.newBuilder().setInstrumentationType(InstrumentationType.BuiltIn).setInstrumentationSourceName(SpringAnnotationVisitor.class.getName()).setDispatcher(true);
                                        final String fullPath = SpringAnnotationVisitor.getPath(ClassVisitor.this.rootPath, MethodVisitor.this.path, MethodVisitor.this.httpMethod);
                                        builder.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "SpringController", fullPath);
                                        context.addTrace(new Method(methodName, methodDesc), builder.build());
                                    }
                                }
                            };
                        }
                        return super.visitAnnotation(desc, visible);
                    }
                };
            }
        };
    }
    
    static String getPath(final String rootPath, final String methodPath, final String httpMethod) {
        final StringBuilder fullPath = new StringBuilder();
        if (rootPath != null) {
            if (rootPath.endsWith("/")) {
                fullPath.append(rootPath.substring(0, rootPath.length() - 1));
            }
            else {
                fullPath.append(rootPath);
            }
        }
        if (methodPath != null) {
            if (!methodPath.startsWith("/")) {
                fullPath.append('/');
            }
            if (methodPath.endsWith("/")) {
                fullPath.append(methodPath.substring(0, methodPath.length() - 1));
            }
            else {
                fullPath.append(methodPath);
            }
        }
        fullPath.append(" (").append(httpMethod).append(')');
        return fullPath.toString();
    }
    
    static {
        CONTROLLER_DESCRIPTORS = ImmutableSet.of(Type.getObjectType("org/springframework/stereotype/Controller").getDescriptor(), Type.getObjectType("org/springframework/web/bind/annotation/RestController").getDescriptor());
        REQUEST_MAPPING_DESCRIPTOR = Type.getObjectType("org/springframework/web/bind/annotation/RequestMapping").getDescriptor();
    }
}
