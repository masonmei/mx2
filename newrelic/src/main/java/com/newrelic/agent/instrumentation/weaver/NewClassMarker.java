// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.api.agent.weaver.internal.NewClass;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class NewClassMarker
{
    private static final String NEW_CLASS_INTERNAL_NAME;
    
    static ClassVisitor getVisitor(final ClassVisitor cv, final String implementationTitle, final String implementationVersion) {
        return new ClassVisitor(327680, cv) {
            public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                final String[] newInterfaces = new String[interfaces.length + 1];
                System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
                newInterfaces[interfaces.length] = Type.getInternalName(NewClass.class);
                super.visit(version, access, name, signature, superName, newInterfaces);
                final AnnotationVisitor visitor = super.visitAnnotation(Type.getDescriptor(WeaveInstrumentation.class), true);
                visitor.visit("title", implementationTitle);
                visitor.visit("version", implementationVersion);
                visitor.visitEnd();
            }
        };
    }
    
    public static boolean isNewWeaveClass(final ClassReader reader) {
        for (final String className : reader.getInterfaces()) {
            if (NewClassMarker.NEW_CLASS_INTERNAL_NAME.equals(className)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        NEW_CLASS_INTERNAL_NAME = Type.getInternalName(NewClass.class);
    }
}
