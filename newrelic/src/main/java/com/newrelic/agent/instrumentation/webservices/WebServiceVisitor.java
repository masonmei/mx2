// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.webservices;

import javax.jws.WebService;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import javax.jws.WebMethod;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.util.asm.AnnotationDetails;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;

public class WebServiceVisitor implements ClassMatchVisitorFactory
{
    private static final String WEB_SERVICE_ANNOTATION_DESCRIPTOR;
    
    public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
        if (reader.getInterfaces().length == 0) {
            return null;
        }
        return new ClassVisitor(cv) {
            Map<Method, AnnotationDetails> methodsToInstrument;
            Map<String, String> classWebServiceAnnotationDetails;
            String webServiceAnnotationNameValue;
            
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (WebServiceVisitor.WEB_SERVICE_ANNOTATION_DESCRIPTOR.equals(desc)) {
                    this.methodsToInstrument = (Map<Method, AnnotationDetails>)Maps.newHashMap();
                    this.classWebServiceAnnotationDetails = (Map<String, String>)Maps.newHashMap();
                    for (final String interfaceName : reader.getInterfaces()) {
                        try {
                            final ClassStructure classStructure = ClassStructure.getClassStructure(Utils.getClassResource(loader, interfaceName), 15);
                            final AnnotationDetails webServiceDetails = classStructure.getClassAnnotations().get(WebServiceVisitor.WEB_SERVICE_ANNOTATION_DESCRIPTOR);
                            if (webServiceDetails != null) {
                                this.webServiceAnnotationNameValue = (String)webServiceDetails.getValue("name");
                                for (final Method m : classStructure.getMethods()) {
                                    final Map<String, AnnotationDetails> methodAnnotations = classStructure.getMethodAnnotations(m);
                                    final AnnotationDetails webMethodDetails = methodAnnotations.get(Type.getDescriptor(WebMethod.class));
                                    this.methodsToInstrument.put(m, webMethodDetails);
                                }
                            }
                        }
                        catch (Exception e) {
                            Agent.LOG.log(Level.FINEST, e.toString(), e);
                        }
                    }
                    return new WebServiceAnnotationVisitor(super.visitAnnotation(desc, visible));
                }
                return super.visitAnnotation(desc, visible);
            }
            
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                if (this.methodsToInstrument != null) {
                    final Method method = new Method(name, desc);
                    if (this.methodsToInstrument.containsKey(method)) {
                        final AnnotationDetails webMethod = this.methodsToInstrument.get(method);
                        String className = this.classWebServiceAnnotationDetails.get("endpointInterface");
                        if (className == null) {
                            className = Type.getObjectType(reader.getClassName()).getClassName();
                        }
                        String operationName = (String)((webMethod == null) ? name : webMethod.getValue("operationName"));
                        if (operationName == null) {
                            operationName = name;
                        }
                        final String txName = Strings.join('/', className, operationName);
                        if (Agent.LOG.isFinerEnabled()) {
                            Agent.LOG.finer("Creating a web service tracer for " + reader.getClassName() + '.' + method + " using transaction name " + txName);
                        }
                        context.addTrace(method, TraceDetailsBuilder.newBuilder().setInstrumentationType(InstrumentationType.BuiltIn).setInstrumentationSourceName(WebServiceVisitor.class.getName()).setDispatcher(true).setWebTransaction(true).setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "WebService", txName).build());
                    }
                }
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            
            class WebServiceAnnotationVisitor extends AnnotationVisitor
            {
                public WebServiceAnnotationVisitor() {
                    super(327680, av);
                }
                
                public void visit(final String name, final Object value) {
                    if (value instanceof String) {
                        ClassVisitor.this.classWebServiceAnnotationDetails.put(name, (String)value);
                    }
                    super.visit(name, value);
                }
            }
        };
    }
    
    static {
        WEB_SERVICE_ANNOTATION_DESCRIPTOR = Type.getDescriptor(WebService.class);
    }
}
