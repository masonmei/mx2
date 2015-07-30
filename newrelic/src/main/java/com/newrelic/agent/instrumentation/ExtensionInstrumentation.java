// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.security.ProtectionDomain;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import com.newrelic.agent.util.InstrumentationWrapper;

class ExtensionInstrumentation extends InstrumentationWrapper
{
    private final MultiClassFileTransformer transformer;
    private final MultiClassFileTransformer retransformingTransformer;
    
    public ExtensionInstrumentation(final Instrumentation delegate) {
        super(delegate);
        this.transformer = new MultiClassFileTransformer();
        this.retransformingTransformer = new MultiClassFileTransformer();
        delegate.addTransformer(this.transformer);
        delegate.addTransformer(this.retransformingTransformer, true);
    }
    
    public void addTransformer(final ClassFileTransformer transformer, final boolean canRetransform) {
        if (canRetransform) {
            this.retransformingTransformer.addTransformer(transformer);
        }
        else {
            this.transformer.addTransformer(transformer);
        }
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        this.transformer.addTransformer(transformer);
    }
    
    public boolean removeTransformer(final ClassFileTransformer transformer) {
        return !this.transformer.removeTransformer(transformer) && this.retransformingTransformer.removeTransformer(transformer);
    }
    
    private static final class MultiClassFileTransformer implements ClassFileTransformer
    {
        private final List<ClassFileTransformer> transformers;
        
        private MultiClassFileTransformer() {
            this.transformers = Lists.newCopyOnWriteArrayList();
        }
        
        public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            final byte[] originalBytes = classfileBuffer;
            for (final ClassFileTransformer transformer : this.transformers) {
                try {
                    final byte[] newBytes = transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    if (null == newBytes) {
                        continue;
                    }
                    classfileBuffer = newBytes;
                }
                catch (Throwable t) {
                    Agent.LOG.log(Level.FINE, "An error occurred transforming class {0} : {1}", new Object[] { className, t.getMessage() });
                    Agent.LOG.log(Level.FINEST, t, t.getMessage(), new Object[0]);
                }
            }
            return (byte[])((originalBytes == classfileBuffer) ? null : classfileBuffer);
        }
        
        public boolean removeTransformer(final ClassFileTransformer transformer) {
            return this.transformers.remove(transformer);
        }
        
        public void addTransformer(final ClassFileTransformer transformer) {
            this.transformers.add(transformer);
        }
    }
}
