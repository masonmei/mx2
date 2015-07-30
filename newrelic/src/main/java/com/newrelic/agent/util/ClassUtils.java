// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Set;
import java.lang.reflect.Method;

public class ClassUtils
{
    public static Method findSuperDefinition(final Method method) {
        return findSuperDefinition(method.getDeclaringClass(), method);
    }
    
    private static Method findSuperDefinition(final Class<?> clazz, Method method) {
        final Class[] arr$;
        final Class<?>[] interfaces = (Class<?>[])(arr$ = clazz.getInterfaces());
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final Class<?> interfaceClass = (Class<?>)arr$[i$];
            try {
                return interfaceClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            }
            catch (Exception e) {
                method = findSuperDefinition(interfaceClass, method);
                ++i$;
                continue;
            }
            break;
        }
        final Class<?> parentClass = clazz.getSuperclass();
        if (parentClass != null) {
            try {
                method = parentClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            }
            catch (NoSuchMethodException ex) {}
            return findSuperDefinition(parentClass, method);
        }
        return method;
    }
    
    public static Set<String> getClassReferences(final byte[] classBytes) {
        final ClassReader cr = new ClassReader(classBytes);
        final Set<String> classNames = (Set<String>)Sets.newHashSet();
        final ClassVisitor cv = new ClassVisitor(327680) {
            public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                this.addType(Type.getObjectType(superName));
                for (final String iFace : interfaces) {
                    this.addType(Type.getObjectType(iFace));
                }
            }
            
            public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
                this.addType(Type.getType(desc));
                return null;
            }
            
            private void addType(final Type type) {
                if (type == null) {
                    return;
                }
                if (type.getSort() == 9) {
                    this.addType(type.getElementType());
                }
                else if (type.getSort() == 10) {
                    classNames.add(type.getInternalName());
                }
            }
            
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                this.addMethodClasses(name, desc);
                return new MethodVisitor(327680) {
                    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
                        ClassVisitor.this.addType(Type.getType(desc));
                    }
                    
                    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                        ClassVisitor.this.addMethodClasses(name, desc);
                    }
                    
                    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
                        ClassVisitor.this.addType(Type.getType(desc));
                    }
                };
            }
            
            private void addMethodClasses(final String name, final String desc) {
                final com.newrelic.agent.deps.org.objectweb.asm.commons.Method method = new com.newrelic.agent.deps.org.objectweb.asm.commons.Method(name, desc);
                for (final Type t : method.getArgumentTypes()) {
                    this.addType(t);
                }
            }
        };
        cr.accept(cv, 4);
        return classNames;
    }
}
