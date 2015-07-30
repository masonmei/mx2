// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.adapters;

import com.newrelic.agent.deps.javassist.bytecode.Descriptor;
import java.util.Arrays;
import com.newrelic.agent.deps.com.google.common.base.Joiner;
import com.newrelic.agent.deps.javassist.bytecode.AccessFlag;
import com.newrelic.agent.deps.org.reflections.util.Utils;
import java.io.IOException;
import com.newrelic.agent.deps.org.reflections.ReflectionsException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import com.newrelic.agent.deps.org.reflections.vfs.Vfs;
import com.newrelic.agent.deps.javassist.bytecode.annotation.Annotation;
import java.util.Iterator;
import java.util.Collection;
import com.newrelic.agent.deps.javassist.bytecode.ParameterAnnotationsAttribute;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.javassist.bytecode.AnnotationsAttribute;
import java.util.List;
import com.newrelic.agent.deps.javassist.bytecode.MethodInfo;
import com.newrelic.agent.deps.javassist.bytecode.FieldInfo;
import com.newrelic.agent.deps.javassist.bytecode.ClassFile;

public class JavassistAdapter implements MetadataAdapter<ClassFile, FieldInfo, MethodInfo>
{
    public static boolean includeInvisibleTag;
    
    public List<FieldInfo> getFields(final ClassFile cls) {
        return (List<FieldInfo>)cls.getFields();
    }
    
    public List<MethodInfo> getMethods(final ClassFile cls) {
        return (List<MethodInfo>)cls.getMethods();
    }
    
    public String getMethodName(final MethodInfo method) {
        return method.getName();
    }
    
    public List<String> getParameterNames(final MethodInfo method) {
        String descriptor = method.getDescriptor();
        descriptor = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.lastIndexOf(")"));
        return this.splitDescriptorToTypeNames(descriptor);
    }
    
    public List<String> getClassAnnotationNames(final ClassFile aClass) {
        return this.getAnnotationNames((AnnotationsAttribute)aClass.getAttribute("RuntimeVisibleAnnotations"), JavassistAdapter.includeInvisibleTag ? ((AnnotationsAttribute)aClass.getAttribute("RuntimeInvisibleAnnotations")) : null);
    }
    
    public List<String> getFieldAnnotationNames(final FieldInfo field) {
        return this.getAnnotationNames((AnnotationsAttribute)field.getAttribute("RuntimeVisibleAnnotations"), JavassistAdapter.includeInvisibleTag ? ((AnnotationsAttribute)field.getAttribute("RuntimeInvisibleAnnotations")) : null);
    }
    
    public List<String> getMethodAnnotationNames(final MethodInfo method) {
        return this.getAnnotationNames((AnnotationsAttribute)method.getAttribute("RuntimeVisibleAnnotations"), JavassistAdapter.includeInvisibleTag ? ((AnnotationsAttribute)method.getAttribute("RuntimeInvisibleAnnotations")) : null);
    }
    
    public List<String> getParameterAnnotationNames(final MethodInfo method, final int parameterIndex) {
        final List<String> result = (List<String>)Lists.newArrayList();
        final List<ParameterAnnotationsAttribute> parameterAnnotationsAttributes = Lists.newArrayList((ParameterAnnotationsAttribute)method.getAttribute("RuntimeVisibleParameterAnnotations"), (ParameterAnnotationsAttribute)method.getAttribute("RuntimeInvisibleParameterAnnotations"));
        if (parameterAnnotationsAttributes != null) {
            for (final ParameterAnnotationsAttribute parameterAnnotationsAttribute : parameterAnnotationsAttributes) {
                if (parameterAnnotationsAttribute != null) {
                    final Annotation[][] annotations = parameterAnnotationsAttribute.getAnnotations();
                    if (parameterIndex >= annotations.length) {
                        continue;
                    }
                    final Annotation[] annotation = annotations[parameterIndex];
                    result.addAll(this.getAnnotationNames(annotation));
                }
            }
        }
        return result;
    }
    
    public String getReturnTypeName(final MethodInfo method) {
        String descriptor = method.getDescriptor();
        descriptor = descriptor.substring(descriptor.lastIndexOf(")") + 1);
        return this.splitDescriptorToTypeNames(descriptor).get(0);
    }
    
    public String getFieldName(final FieldInfo field) {
        return field.getName();
    }
    
    public ClassFile getOfCreateClassObject(final Vfs.File file) {
        InputStream inputStream = null;
        try {
            inputStream = file.openInputStream();
            final DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
            return new ClassFile(dis);
        }
        catch (IOException e) {
            throw new ReflectionsException("could not create class file from " + file.getName(), e);
        }
        finally {
            Utils.close(inputStream);
        }
    }
    
    public String getMethodModifier(final MethodInfo method) {
        final int accessFlags = method.getAccessFlags();
        return AccessFlag.isPrivate(accessFlags) ? "private" : (AccessFlag.isProtected(accessFlags) ? "protected" : (this.isPublic(accessFlags) ? "public" : ""));
    }
    
    public String getMethodKey(final ClassFile cls, final MethodInfo method) {
        return this.getMethodName(method) + "(" + Joiner.on(", ").join(this.getParameterNames(method)) + ")";
    }
    
    public String getMethodFullKey(final ClassFile cls, final MethodInfo method) {
        return this.getClassName(cls) + "." + this.getMethodKey(cls, method);
    }
    
    public boolean isPublic(final Object o) {
        final Integer accessFlags = (o instanceof ClassFile) ? ((ClassFile)o).getAccessFlags() : ((o instanceof FieldInfo) ? ((FieldInfo)o).getAccessFlags() : ((o instanceof MethodInfo) ? ((MethodInfo)o).getAccessFlags() : null));
        return accessFlags != null && AccessFlag.isPublic(accessFlags);
    }
    
    public String getClassName(final ClassFile cls) {
        return cls.getName();
    }
    
    public String getSuperclassName(final ClassFile cls) {
        return cls.getSuperclass();
    }
    
    public List<String> getInterfacesNames(final ClassFile cls) {
        return Arrays.asList(cls.getInterfaces());
    }
    
    public boolean acceptsInput(final String file) {
        return file.endsWith(".class");
    }
    
    private List<String> getAnnotationNames(final AnnotationsAttribute... annotationsAttributes) {
        final List<String> result = (List<String>)Lists.newArrayList();
        if (annotationsAttributes != null) {
            for (final AnnotationsAttribute annotationsAttribute : annotationsAttributes) {
                if (annotationsAttribute != null) {
                    for (final Annotation annotation : annotationsAttribute.getAnnotations()) {
                        result.add(annotation.getTypeName());
                    }
                }
            }
        }
        return result;
    }
    
    private List<String> getAnnotationNames(final Annotation[] annotations) {
        final List<String> result = (List<String>)Lists.newArrayList();
        for (final Annotation annotation : annotations) {
            result.add(annotation.getTypeName());
        }
        return result;
    }
    
    private List<String> splitDescriptorToTypeNames(final String descriptors) {
        final List<String> result = (List<String>)Lists.newArrayList();
        if (descriptors != null && descriptors.length() != 0) {
            final List<Integer> indices = (List<Integer>)Lists.newArrayList();
            final Descriptor.Iterator iterator = new Descriptor.Iterator(descriptors);
            while (iterator.hasNext()) {
                indices.add(iterator.next());
            }
            indices.add(descriptors.length());
            for (int i = 0; i < indices.size() - 1; ++i) {
                final String s1 = Descriptor.toString(descriptors.substring(indices.get(i), indices.get(i + 1)));
                result.add(s1);
            }
        }
        return result;
    }
    
    static {
        JavassistAdapter.includeInvisibleTag = true;
    }
}
