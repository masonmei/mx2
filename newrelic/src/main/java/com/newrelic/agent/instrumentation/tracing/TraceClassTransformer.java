// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.lang.instrument.IllegalClassFormatException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.security.ProtectionDomain;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;

public class TraceClassTransformer implements ContextClassTransformer
{
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
        try {
            return this.doTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, context);
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINE, "Unable to transform class " + className, t);
            return null;
        }
    }
    
    private byte[] doTransform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context) throws IllegalClassFormatException {
        if (!context.isTracerMatch()) {
            return null;
        }
        Agent.LOG.debug("Instrumenting class " + className);
        final ClassReader reader = new ClassReader(classfileBuffer);
        ClassVisitor cv;
        final ClassWriter writer = (ClassWriter)(cv = new ClassWriter(1));
        cv = new TraceClassVisitor(cv, className, context);
        reader.accept(cv, 8);
        return writer.toByteArray();
    }
}
