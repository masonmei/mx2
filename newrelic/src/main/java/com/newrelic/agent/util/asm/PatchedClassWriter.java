// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import java.util.Set;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.io.InputStream;
import java.io.IOException;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;

public class PatchedClassWriter extends ClassWriter
{
    private static final String JAVA_LANG_OBJECT = "java/lang/Object";
    protected final ClassResolver classResolver;
    
    public PatchedClassWriter(final int flags, final ClassLoader classLoader) {
        this(flags, ClassResolvers.getClassLoaderResolver((classLoader == null) ? ClassLoader.getSystemClassLoader() : classLoader));
    }
    
    public PatchedClassWriter(final int flags, final ClassResolver classResolver) {
        super(flags);
        this.classResolver = classResolver;
    }
    
    protected String getCommonSuperClass(final String type1, final String type2) {
        if (type1.equals(type2)) {
            return type1;
        }
        if ("java/lang/Object".equals(type1) || "java/lang/Object".equals(type2)) {
            return "java/lang/Object";
        }
        try {
            final ClassReader reader1 = this.getClassReader(type1);
            final ClassReader reader2 = this.getClassReader(type2);
            if (reader1 == null || reader2 == null) {
                return "java/lang/Object";
            }
            final String superClass = this.getCommonSuperClass(reader1, reader2);
            if (superClass == null) {
                return "java/lang/Object";
            }
            return superClass;
        }
        catch (Exception ex) {
            Agent.LOG.log(Level.FINER, (Throwable)ex, "Unable to get common super class", new Object[0]);
            throw new RuntimeException(ex.toString());
        }
    }
    
    protected ClassReader getClassReader(final String type) throws IOException {
        InputStream classResource = null;
        try {
            classResource = this.classResolver.getClassResource(type);
            final ClassReader classReader = (classResource == null) ? null : new ClassReader(classResource);
            if (classResource != null) {
                classResource.close();
            }
            return classReader;
        }
        catch (IOException ex) {
            Agent.LOG.log(Level.FINEST, ex.toString(), ex);
            final ClassReader classReader2 = null;
            if (classResource != null) {
                classResource.close();
            }
            return classReader2;
        }
        finally {
            if (classResource != null) {
                classResource.close();
            }
        }
    }
    
    private String getCommonSuperClass(ClassReader reader1, ClassReader reader2) throws ClassNotFoundException, IOException {
        if (this.isAssignableFrom(reader1, reader2)) {
            return reader1.getClassName();
        }
        if (this.isAssignableFrom(reader2, reader1)) {
            return reader2.getClassName();
        }
        if (this.isInterface(reader1) || this.isInterface(reader2)) {
            return "java/lang/Object";
        }
        final Set<String> classes = (Set<String>)Sets.newHashSet();
        classes.add(reader1.getClassName());
        while (reader1.getSuperName() != null) {
            classes.add(reader1.getSuperName());
            reader1 = this.getClassReader(reader1.getSuperName());
        }
        while (reader2.getSuperName() != null) {
            if (classes.contains(reader2.getClassName())) {
                return reader2.getClassName();
            }
            reader2 = this.getClassReader(reader2.getSuperName());
        }
        return null;
    }
    
    private boolean isInterface(final ClassReader reader) {
        return (reader.getAccess() & 0x200) != 0x0;
    }
    
    private boolean isAssignableFrom(final ClassReader reader1, final ClassReader reader2) {
        return reader1.getClassName().equals(reader2.getClassName()) || reader1.getClassName().equals(reader2.getSuperName());
    }
}
