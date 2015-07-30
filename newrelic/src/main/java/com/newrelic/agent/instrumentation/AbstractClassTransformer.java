// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import com.newrelic.agent.InstrumentationProxy;
import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import java.security.ProtectionDomain;

public abstract class AbstractClassTransformer implements StartableClassFileTransformer
{
    private final int classreaderFlags;
    private final boolean enabled;
    
    public AbstractClassTransformer(final int classreaderFlags) {
        this(classreaderFlags, true);
    }
    
    public AbstractClassTransformer(final int classreaderFlags, final boolean enabled) {
        this.enabled = enabled;
        this.classreaderFlags = classreaderFlags;
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (!this.matches(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)) {
                return null;
            }
            if (!this.isAbleToResolveAgent(loader, className)) {
                final String msg = MessageFormat.format("Not instrumenting {0}: class loader unable to load agent classes", className);
                Agent.LOG.log(Level.FINER, msg);
                return null;
            }
            final byte[] classBytesWithUID = InstrumentationUtils.generateClassBytesWithSerialVersionUID(classfileBuffer, this.classreaderFlags, loader);
            final ClassReader cr = new ClassReader(classBytesWithUID);
            final ClassWriter cw = InstrumentationUtils.getClassWriter(cr, loader);
            final ClassVisitor classVisitor = this.getClassVisitor(cr, cw, className, loader);
            if (null == classVisitor) {
                return null;
            }
            cr.accept(classVisitor, this.classreaderFlags);
            final String msg2 = MessageFormat.format("Instrumenting {0}", className);
            Agent.LOG.finer(msg2);
            return cw.toByteArray();
        }
        catch (StopProcessingException e) {
            final String msg3 = MessageFormat.format("Instrumentation aborted for {0}: {1}", className, e);
            Agent.LOG.log(Level.FINER, msg3, e);
            return null;
        }
        catch (Throwable t) {
            final String msg3 = MessageFormat.format("Instrumentation error for {0}: {1}", className, t);
            Agent.LOG.log(Level.FINER, msg3, t);
            return null;
        }
    }
    
    protected boolean isAbleToResolveAgent(final ClassLoader loader, final String className) {
        return InstrumentationUtils.isAbleToResolveAgent(loader, className);
    }
    
    protected int getClassReaderFlags() {
        return this.classreaderFlags;
    }
    
    public void start(final InstrumentationProxy instrumentation, final boolean isRetransformSupported) {
        final boolean canRetransform = isRetransformSupported && this.isRetransformSupported();
        if (this.isEnabled()) {
            instrumentation.addTransformer(this, canRetransform);
            this.start();
        }
    }
    
    protected void start() {
    }
    
    protected boolean isEnabled() {
        return this.enabled;
    }
    
    protected abstract boolean isRetransformSupported();
    
    protected abstract ClassVisitor getClassVisitor(final ClassReader p0, final ClassWriter p1, final String p2, final ClassLoader p3);
    
    protected abstract boolean matches(final ClassLoader p0, final String p1, final Class<?> p2, final ProtectionDomain p3, final byte[] p4);
}
