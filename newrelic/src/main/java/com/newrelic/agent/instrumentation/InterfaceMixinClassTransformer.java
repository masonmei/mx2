// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.lang.instrument.IllegalClassFormatException;
import java.text.MessageFormat;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import com.newrelic.agent.util.Strings;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Iterator;
import java.util.Collection;
import java.lang.annotation.Annotation;
import com.newrelic.agent.util.Annotations;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceMixinClassTransformer extends AbstractClassTransformer
{
    private final Map<String, List<Class<?>>> interfaceVisitors;
    
    public InterfaceMixinClassTransformer(final int classreaderFlags) {
        super(classreaderFlags);
        this.interfaceVisitors = new HashMap<String, List<Class<?>>>();
    }
    
    protected void start() {
        this.addInterfaceMixins();
    }
    
    private void addInterfaceMixins() {
        final Collection<Class<?>> classes = Annotations.getAnnotationClassesFromManifest(InterfaceMixin.class, "com/newrelic/agent/instrumentation/pointcuts");
        for (final Class<?> clazz : classes) {
            this.addInterfaceMixin(clazz);
        }
    }
    
    protected void addInterfaceMixin(final Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        final InterfaceMixin mixin = clazz.getAnnotation(InterfaceMixin.class);
        if (mixin == null) {
            Agent.LOG.log(Level.FINER, "InterfaceMixin access failed: " + clazz.getName());
            return;
        }
        for (final String className : mixin.originalClassName()) {
            final String key = Strings.fixInternalClassName(className);
            List<Class<?>> value = this.interfaceVisitors.get(key);
            if (value == null) {
                value = new ArrayList<Class<?>>(1);
            }
            value.add(clazz);
            this.interfaceVisitors.put(key, value);
        }
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (!this.matches(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)) {
                return null;
            }
            if (!this.isAbleToResolveAgent(loader, className)) {
                return null;
            }
            final List<Class<?>> clazzes = this.interfaceVisitors.get(className);
            if (clazzes == null || clazzes.size() == 0) {
                return null;
            }
            final byte[] classBytesWithUID = InstrumentationUtils.generateClassBytesWithSerialVersionUID(classfileBuffer, this.getClassReaderFlags(), loader);
            return this.transform(classBytesWithUID, clazzes, loader, className);
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Instrumentation error for {0}: {1}", className, t);
            Agent.LOG.log(Level.FINER, msg, t);
            return null;
        }
    }
    
    private byte[] transform(final byte[] classBytesWithUID, final List<Class<?>> clazzes, final ClassLoader loader, final String className) throws Exception {
        byte[] classBytes = classBytesWithUID;
        byte[] oldClassBytes = classBytesWithUID;
        for (final Class<?> clazz : clazzes) {
            try {
                classBytes = (oldClassBytes = this.transform(classBytes, clazz, loader, className));
            }
            catch (StopProcessingException e) {
                final String msg = MessageFormat.format("Failed to append {0} to {1}: {2}", clazz.getName(), className, e);
                Agent.LOG.fine(msg);
                classBytes = oldClassBytes;
            }
        }
        final String msg2 = MessageFormat.format("Instrumenting {0}", className);
        Agent.LOG.finer(msg2);
        return classBytes;
    }
    
    private byte[] transform(final byte[] classBytes, final Class<?> clazz, final ClassLoader loader, final String className) throws Exception {
        final ClassReader cr = new ClassReader(classBytes);
        final ClassWriter cw = InstrumentationUtils.getClassWriter(cr, loader);
        final ClassVisitor classVisitor = this.getClassVisitor(cw, className, clazz, loader);
        cr.accept(classVisitor, this.getClassReaderFlags());
        Agent.LOG.log(Level.FINEST, "InterfaceMixingClassTransformer.transform(bytes, clazz, {0}, {1})", new Object[] { loader, className });
        return cw.toByteArray();
    }
    
    private ClassVisitor getClassVisitor(final ClassVisitor classVisitor, final String className, final Class<?> clazz, final ClassLoader loader) {
        ClassVisitor adapter = new AddInterfaceAdapter(classVisitor, className, clazz);
        adapter = RequireMethodsAdapter.getRequireMethodsAdaptor(adapter, className, clazz, loader);
        adapter = new FieldAccessorGeneratingClassAdapter(adapter, className, clazz);
        return adapter;
    }
    
    protected boolean isRetransformSupported() {
        return false;
    }
    
    protected boolean matches(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        return this.interfaceVisitors.containsKey(className);
    }
    
    protected ClassVisitor getClassVisitor(final ClassReader cr, final ClassWriter cw, final String className, final ClassLoader loader) {
        return null;
    }
}
