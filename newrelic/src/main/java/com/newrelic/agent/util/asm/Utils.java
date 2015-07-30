// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.util.TraceClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.util.Printer;
import com.newrelic.agent.deps.com.google.common.base.Joiner;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.util.TraceMethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.util.Textifier;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;
import java.io.OutputStream;
import java.io.PrintWriter;
import com.newrelic.agent.util.BootstrapLoader;
import com.newrelic.agent.Agent;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.lang.reflect.Proxy;
import java.lang.reflect.Modifier;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Set;

public class Utils
{
    private static final String PROXY_CLASS_NAME = "java/lang/reflect/Proxy";
    private static final Set<String> JAXB_SUPERCLASSES;
    private static final Set<String> RMI_SUPERCLASSES;
    private static final Set<String> PRIMITIVE_TYPES;
    
    public static boolean isJdkProxy(final byte[] classBytes) {
        final ClassReader reader = new ClassReader(classBytes);
        return isJdkProxy(reader);
    }
    
    public static boolean isJdkProxy(final ClassReader reader) {
        if (reader != null && looksLikeAProxy(reader)) {
            final ProxyClassVisitor cv = new ProxyClassVisitor();
            reader.accept(cv, 1);
            return cv.isProxy();
        }
        return false;
    }
    
    private static boolean looksLikeAProxy(final ClassReader reader) {
        return "java/lang/reflect/Proxy".equals(reader.getSuperName()) && Modifier.isFinal(reader.getAccess());
    }
    
    public static ClassReader readClass(final Class<?> theClass) throws IOException, BenignClassReadException {
        if (theClass.isArray()) {
            throw new BenignClassReadException(theClass.getName() + " is an array");
        }
        if (Proxy.isProxyClass(theClass)) {
            throw new BenignClassReadException(theClass.getName() + " is a Proxy class");
        }
        if (isRMIStubOrProxy(theClass)) {
            throw new BenignClassReadException(theClass.getName() + " is an RMI Stub or Proxy class");
        }
        if (theClass.getName().startsWith("sun.reflect.")) {
            throw new BenignClassReadException(theClass.getName() + " is a reflection class");
        }
        if (isJAXBClass(theClass)) {
            throw new BenignClassReadException(theClass.getName() + " is a JAXB accessor class");
        }
        if (theClass.getProtectionDomain().getCodeSource() != null && theClass.getProtectionDomain().getCodeSource().getLocation() == null) {
            throw new BenignClassReadException(theClass.getName() + " is a generated class");
        }
        final URL resource = getClassResource(theClass.getClassLoader(), Type.getInternalName(theClass));
        if (resource == null) {
            final ClassReader reader = ServiceFactory.getClassTransformerService().getContextManager().getClassWeaverService().getClassReader(theClass);
            if (reader != null) {
                return reader;
            }
        }
        return getClassReaderFromResource(theClass.getName(), resource);
    }
    
    private static boolean isJAXBClass(final Class<?> theClass) {
        return theClass.getSuperclass() != null && Utils.JAXB_SUPERCLASSES.contains(theClass.getSuperclass().getName());
    }
    
    private static boolean isRMIStubOrProxy(final Class<?> theClass) {
        return theClass.getSuperclass() != null && Utils.RMI_SUPERCLASSES.contains(theClass.getSuperclass().getName());
    }
    
    public static ClassReader readClass(final ClassLoader loader, final String internalClassName) throws IOException {
        final URL resource = getClassResource(loader, internalClassName);
        return getClassReaderFromResource(internalClassName, resource);
    }
    
    public static ClassReader getClassReaderFromResource(final String internalClassName, final URL resource) throws IOException {
        if (resource != null) {
            final InputStream stream = resource.openStream();
            try {
                final ClassReader classReader = new ClassReader(stream);
                stream.close();
                return classReader;
            }
            finally {
                stream.close();
            }
        }
        throw new MissingResourceException("Unable to get the resource stream for class " + internalClassName);
    }
    
    public static String getClassResourceName(final String internalName) {
        return internalName + ".class";
    }
    
    public static String getClassResourceName(final Class<?> clazz) {
        return getClassResourceName(Type.getInternalName(clazz));
    }
    
    public static URL getClassResource(final ClassLoader loader, final Type type) {
        return getClassResource(loader, type.getInternalName());
    }
    
    public static URL getClassResource(ClassLoader loader, final String internalClassName) {
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        if (Agent.LOG.isFinestEnabled() && internalClassName.endsWith(".class.class")) {
            Agent.LOG.finest("Invalid resource name " + internalClassName);
        }
        URL url = loader.getResource(getClassResourceName(internalClassName));
        if (url == null) {
            url = BootstrapLoader.get().getBootstrapResource(internalClassName);
        }
        return url;
    }
    
    public static void print(final byte[] bytes) {
        print(bytes, new PrintWriter(System.out, true));
    }
    
    public static String asString(final MethodNode method) {
        final Printer printer = new Textifier();
        final TraceMethodVisitor tv = new TraceMethodVisitor(printer);
        method.accept(tv);
        return Joiner.on(' ').join(printer.getText());
    }
    
    public static void print(final byte[] bytes, final PrintWriter pw) {
        final ClassReader cr = new ClassReader(bytes);
        final TraceClassVisitor mv = new TraceClassVisitor(pw);
        cr.accept(mv, 8);
        pw.flush();
    }
    
    public static boolean isPrimitiveType(final String type) {
        return Utils.PRIMITIVE_TYPES.contains(type);
    }
    
    public static int getFirstLocal(final int access, final Method method) {
        final Type[] argumentTypes = method.getArgumentTypes();
        int nextLocal = ((0x8 & access) == 0x0) ? 1 : 0;
        for (int i = 0; i < argumentTypes.length; ++i) {
            nextLocal += argumentTypes[i].getSize();
        }
        return nextLocal;
    }
    
    static {
        JAXB_SUPERCLASSES = ImmutableSet.of("com.sun.xml.internal.bind.v2.runtime.reflect.Accessor", "com.sun.xml.bind.v2.runtime.reflect.Accessor", "com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver");
        RMI_SUPERCLASSES = ImmutableSet.of("org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub", "com.sun.jmx.remote.internal.ProxyRef");
        PRIMITIVE_TYPES = ImmutableSet.of(Type.BOOLEAN_TYPE.getClassName(), Type.BYTE_TYPE.getClassName(), Type.CHAR_TYPE.getClassName(), Type.DOUBLE_TYPE.getClassName(), Type.FLOAT_TYPE.getClassName(), Type.INT_TYPE.getClassName(), Type.LONG_TYPE.getClassName(), Type.SHORT_TYPE.getClassName(), Type.VOID_TYPE.getClassName());
    }
}
