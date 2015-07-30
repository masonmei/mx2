// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.util.Queue;
import java.util.Arrays;
import java.util.LinkedList;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.text.MessageFormat;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Collection;
import java.util.HashSet;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class RequireMethodsAdapter extends ClassVisitor
{
    private final Set<Method> requiredMethods;
    private final ClassLoader classLoader;
    private final String className;
    private final String requiredInterface;
    private final ClassVisitor missingMethodsVisitor;
    
    private RequireMethodsAdapter(final ClassVisitor cv, final Set<Method> requiredMethods, final String requiredInterface, final String className, final ClassLoader loader) {
        super(327680, cv);
        this.missingMethodsVisitor = new MissingMethodsVisitor();
        this.className = className;
        this.requiredInterface = requiredInterface;
        this.classLoader = loader;
        this.requiredMethods = new HashSet<Method>(requiredMethods);
    }
    
    public static RequireMethodsAdapter getRequireMethodsAdaptor(final ClassVisitor cv, final String className, final Class<?> type, final ClassLoader loader) {
        final Set<Method> requiredMethods = InstrumentationUtils.getDeclaredMethods(type);
        return new RequireMethodsAdapter(cv, requiredMethods, type.getName(), className, loader);
    }
    
    public static RequireMethodsAdapter getRequireMethodsAdaptor(final ClassVisitor cv, final Set<Method> requiredMethods, final String className, final String requiredInterface, final ClassLoader loader) {
        return new RequireMethodsAdapter(cv, requiredMethods, requiredInterface, className, loader);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        this.requiredMethods.remove(new Method(name, desc));
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    public void visitEnd() {
        if (this.requiredMethods.size() > 0) {
            this.visitSuperclassesOrInterfaces();
        }
        if (this.requiredMethods.size() > 0) {
            final String msg = MessageFormat.format("{0} does not implement these methods: {1} declared in {2}", this.className, this.requiredMethods, this.requiredInterface);
            throw new StopProcessingException(msg);
        }
        super.visitEnd();
    }
    
    private void visitSuperclassesOrInterfaces() {
        final ClassMetadata metadata = new ClassMetadata(this.className, this.classLoader);
        if (metadata.isInterface()) {
            this.visitInterfaces(metadata);
        }
        else {
            this.visitSuperclasses(metadata);
        }
    }
    
    private void visitSuperclasses(final ClassMetadata metadata) {
        for (ClassMetadata superClassMetadata = metadata.getSuperclass(); superClassMetadata != null; superClassMetadata = superClassMetadata.getSuperclass()) {
            final ClassReader cr = superClassMetadata.getClassReader();
            cr.accept(this.missingMethodsVisitor, 0);
            if (this.requiredMethods.size() == 0) {
                return;
            }
        }
    }
    
    private void visitInterfaces(final ClassMetadata metadata) {
        final Queue<String> pendingInterfaces = new LinkedList<String>();
        pendingInterfaces.addAll((Collection<?>)Arrays.asList(metadata.getInterfaceNames()));
        for (String interfaceName = pendingInterfaces.poll(); interfaceName != null; interfaceName = pendingInterfaces.poll()) {
            final ClassMetadata interfaceMetadata = new ClassMetadata(interfaceName, this.classLoader);
            final ClassReader cr = interfaceMetadata.getClassReader();
            cr.accept(this.missingMethodsVisitor, 0);
            if (this.requiredMethods.size() == 0) {
                return;
            }
            pendingInterfaces.addAll((Collection<?>)Arrays.asList(interfaceMetadata.getInterfaceNames()));
        }
    }
    
    private class MissingMethodsVisitor extends ClassVisitor
    {
        private MissingMethodsVisitor() {
            super(327680);
        }
        
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            RequireMethodsAdapter.this.requiredMethods.remove(new Method(name, desc));
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
