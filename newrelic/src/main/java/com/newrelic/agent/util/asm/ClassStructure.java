// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.security.AccessController;
import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedExceptionAction;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import com.newrelic.agent.bridge.reflect.ClassReflection;
import java.lang.reflect.Modifier;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.io.File;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.tree.FieldNode;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;

public class ClassStructure
{
    public static final int METHODS = 1;
    public static final int FIELDS = 2;
    public static final int CLASS_ANNOTATIONS = 4;
    public static final int METHOD_ANNOTATIONS = 8;
    public static final int ALL = 15;
    private Map<Method, MethodDetails> methods;
    private Map<String, FieldNode> fields;
    private final Type type;
    protected final int access;
    protected final String superName;
    protected final String[] interfaces;
    protected Map<String, AnnotationDetails> classAnnotations;
    private static final MethodDetails EMPTY_METHOD_DEFAULTS_MEMBER;
    private static final MethodDetails EMPTY_METHOD_DEFAULTS_STATIC;
    
    private ClassStructure(final String className, final int access, final String superName, final String[] interfaceNames) {
        this.type = Type.getObjectType(className);
        this.access = access;
        this.superName = superName;
        this.interfaces = interfaceNames;
    }
    
    public int getAccess() {
        return this.access;
    }
    
    public String getSuperName() {
        return this.superName;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public Set<Method> getMethods() {
        return this.methods.keySet();
    }
    
    public Map<String, FieldNode> getFields() {
        return this.fields;
    }
    
    public Map<String, AnnotationDetails> getMethodAnnotations(final Method method) {
        final MethodDetails methodDetails = this.methods.get(method);
        if (methodDetails == null) {
            return Collections.emptyMap();
        }
        return methodDetails.annotations;
    }
    
    public Boolean isStatic(final Method method) {
        final MethodDetails methodDetails = this.methods.get(method);
        return (methodDetails == null) ? null : methodDetails.isStatic;
    }
    
    public String[] getInterfaces() {
        return this.interfaces;
    }
    
    public Map<String, AnnotationDetails> getClassAnnotations() {
        return this.classAnnotations;
    }
    
    public String toString() {
        return this.type.getClassName();
    }
    
    public static ClassStructure getClassStructure(final URL url) throws IOException {
        return getClassStructure(url, 1);
    }
    
    public static ClassStructure getClassStructure(final URL url, final int flags) throws IOException {
        return getClassStructure(Utils.getClassReaderFromResource(url.getPath(), url), flags);
    }
    
    public static ClassStructure getClassStructure(final ClassReader cr, final int flags) throws IOException {
        final ClassStructure structure = new ClassStructure(cr.getClassName(), cr.getAccess(), cr.getSuperName(), cr.getInterfaces());
        final ClassVisitor cv = structure.createClassVisitor(flags);
        if (cv != null) {
            cr.accept(cv, 1);
        }
        structure.methods = (structure.methods == null) ? Collections.<Method, MethodDetails>emptyMap() : Collections.unmodifiableMap(structure.methods);
        structure.classAnnotations = (structure.classAnnotations == null) ? Collections.<String, AnnotationDetails>emptyMap() : Collections.unmodifiableMap(structure.classAnnotations);
        structure.fields = (structure.fields == null) ?  Collections.<String, FieldNode>emptyMap() : Collections.unmodifiableMap(structure.fields);
        return structure;
    }
    
    public static ClassStructure getClassStructure(final File jarFile, final String internalName, final int flags) throws IOException, ClassNotFoundException {
        final JarFile jar = new JarFile(jarFile);
        try {
            final JarEntry entry = jar.getJarEntry(internalName + ".class");
            if (entry != null) {
                final InputStream inputStream = jar.getInputStream(entry);
                try {
                    final ClassStructure classStructure = getClassStructure(new ClassReader(inputStream), flags);
                    inputStream.close();
                    jar.close();
                    return classStructure;
                }
                finally {
                    inputStream.close();
                }
            }
            throw new ClassNotFoundException("Unable to find " + internalName + " in " + jarFile.getAbsolutePath());
        }
        finally {
            jar.close();
        }
    }
    
    private ClassVisitor createClassVisitor(final int flags) {
        ClassVisitor cv = null;
        if (isMethodFlagSet(flags)) {
            cv = new ClassVisitor(327680, cv) {
                public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                    if (null == ClassStructure.this.methods) {
                        ClassStructure.this.methods = Maps.newHashMap();
                    }
                    final boolean isStatic = (access & 0x8) == 0x8;
                    final Method method = new Method(name, desc);
                    if ((flags & 0x8) == 0x8) {
                        final MethodDetails details = new MethodDetails(Maps.<String, AnnotationDetails>newHashMap(), isStatic);
                        ClassStructure.this.methods.put(method, details);
                        return new MethodVisitor(327680, super.visitMethod(access, name, desc, signature, exceptions)) {
                            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                                final AnnotationDetails annotation = new AnnotationDetails(super.visitAnnotation(desc, visible), desc);
                                details.annotations.put(desc, annotation);
                                return annotation;
                            }
                        };
                    }
                    ClassStructure.this.methods.put(method, isStatic ? ClassStructure.EMPTY_METHOD_DEFAULTS_STATIC : ClassStructure.EMPTY_METHOD_DEFAULTS_MEMBER);
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
            };
        }
        if ((flags & 0x4) == 0x4) {
            cv = new ClassVisitor(327680, cv) {
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    if (null == ClassStructure.this.classAnnotations) {
                        ClassStructure.this.classAnnotations = Maps.newHashMap();
                    }
                    final AnnotationDetails annotation = new AnnotationDetails(super.visitAnnotation(desc, visible), desc);
                    ClassStructure.this.classAnnotations.put(desc, annotation);
                    return annotation;
                }
            };
        }
        if (isFieldFlagSet(flags)) {
            cv = new ClassVisitor(327680, cv) {
                public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
                    final FieldNode field = new FieldNode(access, name, desc, signature, value);
                    if (ClassStructure.this.fields == null) {
                        ClassStructure.this.fields = Maps.newHashMap();
                    }
                    ClassStructure.this.fields.put(name, field);
                    return super.visitField(access, name, desc, signature, value);
                }
            };
        }
        return cv;
    }
    
    public static ClassStructure getClassStructure(final Class<?> clazz) {
        return getClassStructure(clazz, 1);
    }
    
    public static ClassStructure getClassStructure(final Class<?> clazz, final int flags) {
        int access = 0;
        final int modifiers = clazz.getModifiers();
        String superName = null;
        if (clazz.isAnnotation()) {
            access |= 0x2200;
            if (!Modifier.isPrivate(modifiers)) {
                access |= 0x1;
            }
            superName = "java/lang/Object";
        }
        else if (clazz.isInterface()) {
            access |= 0x200;
            superName = "java/lang/Object";
        }
        else if (clazz.isEnum()) {
            access |= 0x4020;
        }
        else {
            access |= 0x20;
        }
        if (Modifier.isAbstract(modifiers)) {
            access |= 0x400;
        }
        if (!clazz.isAnnotation()) {
            if (Modifier.isPublic(modifiers)) {
                access |= 0x1;
            }
            else if (Modifier.isPrivate(modifiers)) {
                access |= 0x2;
            }
            else if (Modifier.isProtected(modifiers)) {
                access |= 0x4;
            }
        }
        if (Modifier.isFinal(modifiers)) {
            access |= 0x10;
        }
        if (clazz.getSuperclass() != null) {
            superName = Type.getType(clazz.getSuperclass()).getInternalName();
        }
        final String[] interfaces = new String[clazz.getInterfaces().length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfaces[i] = Type.getType(clazz.getInterfaces()[i]).getInternalName();
        }
        final ClassStructure structure = new ClassStructure(Type.getType(clazz).getInternalName(), access, superName, interfaces);
        if ((flags & 0x4) == 0x4) {
            final Annotation[] annotations = clazz.getAnnotations();
            if (annotations.length > 0) {
                structure.classAnnotations = Maps.newHashMap();
                for (final Annotation annotation : annotations) {
                    final AnnotationDetails annotationDetails = getAnnotationDetails(annotation);
                    structure.classAnnotations.put(annotationDetails.desc, annotationDetails);
                }
            }
        }
        if (structure.classAnnotations == null) {
            structure.classAnnotations = Collections.emptyMap();
        }
        if (isFieldFlagSet(flags)) {
            structure.fields = Maps.newHashMap();
            final Field[] arr$2;
            final Field[] declaredFields = arr$2 = ClassReflection.getDeclaredFields((Class)clazz);
            for (final Field f : arr$2) {
                final FieldNode field = new FieldNode(0, f.getName(), Type.getDescriptor(f.getDeclaringClass()), null, null);
                structure.fields.put(f.getName(), field);
            }
        }
        else {
            structure.fields = ImmutableMap.of();
        }
        if (isMethodFlagSet(flags)) {
            structure.methods = Maps.newHashMap();
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                        final java.lang.reflect.Method[] arr$;
                        final java.lang.reflect.Method[] methods = arr$ = ClassReflection.getDeclaredMethods(clazz);
                        for (final java.lang.reflect.Method m : arr$) {
                            structure.methods.put(Method.getMethod(m), getMethodDetails(m, flags, Modifier.isStatic(m.getModifiers())));
                        }
                        return null;
                    }
                });
            }
            catch (Exception ex) {
                Agent.LOG.log(Level.FINEST, "Error getting methods of " + clazz.getName(), ex);
            }
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                        final Constructor[] arr$;
                        final Constructor<?>[] constructors = (Constructor<?>[])(arr$ = ClassReflection.getDeclaredConstructors(clazz));
                        for (final Constructor<?> c : arr$) {
                            structure.methods.put(Method.getMethod(c), getMethodDetails(c, flags, false));
                        }
                        return null;
                    }
                });
            }
            catch (Exception ex) {
                Agent.LOG.log(Level.FINEST, "Error getting constructors of " + clazz.getName(), ex);
            }
        }
        return structure;
    }
    
    private static boolean isMethodFlagSet(final int flags) {
        return (flags & 0x9) > 0;
    }
    
    private static boolean isFieldFlagSet(final int flags) {
        return (flags & 0x2) > 0;
    }
    
    private static MethodDetails getMethodDetails(final AccessibleObject method, final int flags, final boolean isStatic) {
        if ((flags & 0x8) == 0x8) {
            final MethodDetails details = new MethodDetails(Maps.<String, AnnotationDetails>newHashMap(), isStatic);
            for (final Annotation annotation : method.getAnnotations()) {
                final AnnotationDetails annotationDetails = getAnnotationDetails(annotation);
                details.annotations.put(annotationDetails.desc, annotationDetails);
            }
            return details;
        }
        return isStatic ? ClassStructure.EMPTY_METHOD_DEFAULTS_STATIC : ClassStructure.EMPTY_METHOD_DEFAULTS_MEMBER;
    }
    
    private static AnnotationDetails getAnnotationDetails(final Annotation annotation) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        final String annotationDesc = Type.getDescriptor(annotationType);
        final AnnotationDetails node = new AnnotationDetails(null, annotationDesc);
        for (final java.lang.reflect.Method annotationMethod : annotationType.getDeclaredMethods()) {
            try {
                final Object value = annotationMethod.invoke(annotation, new Object[0]);
                node.getOrCreateAttributes().put(annotationMethod.getName(), value);
            }
            catch (Exception e) {
                Agent.LOG.log(Level.FINEST, "Error getting annotation value for " + annotationMethod.getName(), e);
            }
        }
        return node;
    }
    
    static {
        EMPTY_METHOD_DEFAULTS_MEMBER = new MethodDetails(ImmutableMap.<String, AnnotationDetails>of(), false);
        EMPTY_METHOD_DEFAULTS_STATIC = new MethodDetails(ImmutableMap.<String, AnnotationDetails>of(), true);
    }
    
    private static class MethodDetails
    {
        final Map<String, AnnotationDetails> annotations;
        final boolean isStatic;
        
        public MethodDetails(final Map<String, AnnotationDetails> annotations, final boolean isStatic) {
            this.annotations = annotations;
            this.isStatic = isStatic;
        }
    }
}
