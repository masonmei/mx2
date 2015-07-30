// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.webservices;

import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import java.security.ProtectionDomain;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.com.google.common.base.Function;
import java.util.Arrays;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.DefaultClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import java.util.Collection;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.regex.Pattern;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.instrumentation.context.TraceDetailsList;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import java.util.Map;

public class RestAnnotationVisitor
{
    private static final String PATH_DESCRIPTOR;
    private static final Map<String, String> OPERATION_DESCRIPTORS;
    
    public final ClassMatchVisitorFactory getClassMatchVisitorFactory() {
        return new ClassMatchVisitorFactory() {
            public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
                return RestAnnotationVisitor.createClassVisitor(reader.getClassName(), cv, context);
            }
        };
    }
    
    public final ClassMatchVisitorFactory getInterfaceMatchVisitorFactory(final InstrumentationContextManager instrumentationContextManager) {
        final Pattern classMatcherPattern = this.getClassMatcherPattern();
        Agent.LOG.log(Level.FINEST, "REST interface matcher pattern: {0}", new Object[] { classMatcherPattern.pattern() });
        return new ClassMatchVisitorFactory() {
            public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
                if (!classMatcherPattern.matcher(reader.getClassName()).matches()) {
                    return cv;
                }
                final TraceList list = new TraceList();
                return new ClassVisitor(327680, RestAnnotationVisitor.createClassVisitor(reader.getClassName(), cv, list)) {
                    public void visitEnd() {
                        super.visitEnd();
                        if (list.methodToDetails != null) {
                            final List<MethodMatcher> methodMatchers = (List<MethodMatcher>)Lists.newArrayList();
                            for (final Method method : list.methodToDetails.keySet()) {
                                methodMatchers.add(new ExactMethodMatcher(method.getName(), method.getDescriptor()));
                            }
                            final ClassAndMethodMatcher matcher = new DefaultClassAndMethodMatcher(new InterfaceMatcher(reader.getClassName()), OrMethodMatcher.getMethodMatcher(methodMatchers));
                            final ClassMatchVisitorFactory classMatcher = OptimizedClassMatcherBuilder.newBuilder().addClassMethodMatcher(matcher).build();
                            final InterfaceImplementationMatcher interfaceMatcher = new InterfaceImplementationMatcher(reader.getClassName(), list.methodToDetails);
                            instrumentationContextManager.addContextClassTransformer(classMatcher, interfaceMatcher);
                            this.reloadMatchingClasses(classMatcher);
                        }
                    }
                    
                    private void reloadMatchingClasses(final ClassMatchVisitorFactory classMatcher) {
                        ServiceFactory.getClassTransformerService().retransformMatchingClasses(Arrays.asList(classMatcher));
                    }
                };
            }
        };
    }
    
    private Pattern getClassMatcherPattern() {
        try {
            List<String> matchers = (List<String>)ServiceFactory.getConfigService().getDefaultAgentConfig().getValue("instrumentation.rest_annotations.class_filter", (Object)Arrays.asList("com.*"));
            matchers = Lists.transform(matchers, (Function<? super String, ? extends String>)new Function<String, String>() {
                public String apply(final String input) {
                    return input.replace('.', '/');
                }
            });
            return Strings.getPatternFromGlobs(matchers);
        }
        catch (Exception ex) {
            return Pattern.compile("^com/.*");
        }
    }
    
    public static ClassVisitor createClassVisitor(final String internalClassName, final ClassVisitor cv, final TraceDetailsList context) {
        return new ClassVisitor(327680, cv) {
            String rootPath;
            
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (RestAnnotationVisitor.PATH_DESCRIPTOR.equals(desc)) {
                    return new AnnotationVisitor(327680, super.visitAnnotation(desc, visible)) {
                        public void visit(final String name, final Object value) {
                            if ("value".equals(name)) {
                                ClassVisitor.this.rootPath = (String)value;
                            }
                            super.visit(name, value);
                        }
                    };
                }
                return super.visitAnnotation(desc, visible);
            }
            
            public MethodVisitor visitMethod(final int access, final String methodName, final String methodDesc, final String signature, final String[] exceptions) {
                return new MethodVisitor(327680, super.visitMethod(access, methodName, methodDesc, signature, exceptions)) {
                    String methodPath;
                    String httpMethod;
                    
                    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                        final String theHttpMethod = RestAnnotationVisitor.OPERATION_DESCRIPTORS.get(desc);
                        if (theHttpMethod != null) {
                            this.httpMethod = theHttpMethod;
                        }
                        else if (RestAnnotationVisitor.PATH_DESCRIPTOR.equals(desc)) {
                            return new AnnotationVisitor(327680, super.visitAnnotation(desc, visible)) {
                                public void visit(final String name, final Object value) {
                                    if ("value".equals(name)) {
                                        MethodVisitor.this.methodPath = (String)value;
                                    }
                                    super.visit(name, value);
                                }
                            };
                        }
                        return super.visitAnnotation(desc, visible);
                    }
                    
                    public void visitEnd() {
                        if (this.httpMethod != null) {
                            final TraceDetailsBuilder builder = TraceDetailsBuilder.newBuilder().setInstrumentationType(InstrumentationType.BuiltIn).setInstrumentationSourceName(RestAnnotationVisitor.class.getName()).setDispatcher(true);
                            if (ClassVisitor.this.rootPath == null && this.methodPath == null) {
                                builder.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "RestWebService", Type.getObjectType(internalClassName).getClassName() + "/" + methodName);
                            }
                            else {
                                final String fullPath = RestAnnotationVisitor.getPath(ClassVisitor.this.rootPath, this.methodPath, this.httpMethod);
                                builder.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "RestWebService", fullPath);
                            }
                            context.addTrace(new Method(methodName, methodDesc), builder.build());
                        }
                        super.visitEnd();
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
    
    private static Map<String, String> getHttpMethods() {
        final Map<String, String> methods = (Map<String, String>)Maps.newHashMap();
        for (final String httpMethod : Arrays.asList("PUT", "POST", "GET", "DELETE", "HEAD", "OPTIONS")) {
            methods.put(Type.getObjectType("javax/ws/rs/" + httpMethod).getDescriptor(), httpMethod);
        }
        return (Map<String, String>)ImmutableMap.copyOf((Map<?, ?>)methods);
    }
    
    static {
        PATH_DESCRIPTOR = Type.getObjectType("javax/ws/rs/Path").getDescriptor();
        OPERATION_DESCRIPTORS = getHttpMethods();
    }
    
    private static final class InterfaceImplementationMatcher implements ContextClassTransformer
    {
        private final Map<Method, TraceDetails> methodToDetails;
        private final String interfaceName;
        
        public InterfaceImplementationMatcher(final String className, final Map<Method, TraceDetails> methodToDetails) {
            this.interfaceName = className;
            this.methodToDetails = methodToDetails;
        }
        
        public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
            Agent.LOG.log(Level.FINEST, "REST annotation match for interface {0}, class {1}", new Object[] { this.interfaceName, className });
            context.addTracedMethods(this.methodToDetails);
            return null;
        }
    }
    
    private static class TraceList implements TraceDetailsList
    {
        Map<Method, TraceDetails> methodToDetails;
        
        public void addTrace(final Method method, final TraceDetails traceDetails) {
            if (this.methodToDetails == null) {
                this.methodToDetails = (Map<Method, TraceDetails>)Maps.newHashMap();
            }
            this.methodToDetails.put(method, traceDetails);
        }
    }
}
