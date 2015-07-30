// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.HashSet;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.commons.SerialVersionUIDAdder;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;

public class InstrumentationUtils
{
    public static final int JAVA_5_VERSION_NO = 49;
    public static final int JAVA_6_VERSION_NO = 50;
    public static final int JAVA_7_VERSION_NO = 51;
    private static final int JAVA_CLASS_VERSION_BYTE_OFFSET = 6;
    
    public static boolean isAbleToResolveAgent(final ClassLoader loader, final String className) {
        try {
            ClassLoaderCheck.loadAgentClass(loader);
            return true;
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Classloader {0} failed to load Agent class. The agent might need to be loaded by the bootstrap classloader.: {1}", loader.getClass().getName(), t);
            Agent.LOG.finer(msg);
            return false;
        }
    }
    
    public static ClassWriter getClassWriter(final ClassReader cr, final ClassLoader classLoader) {
        int writerFlags = 1;
        if (shouldComputeFrames(cr)) {
            writerFlags = 2;
        }
        return new AgentClassWriter(cr, writerFlags, classLoader);
    }
    
    private static boolean shouldComputeFrames(final ClassReader cr) {
        return getClassJavaVersion(cr) >= 50 && ServiceFactory.getConfigService().getDefaultAgentConfig().getClassTransformerConfig().computeFrames();
    }
    
    public static byte[] generateClassBytesWithSerialVersionUID(final ClassReader classReader, final int classReaderFlags, final ClassLoader classLoader) {
        final ClassWriter cw = getClassWriter(classReader, classLoader);
        final ClassVisitor cv = new SerialVersionUIDAdder(cw);
        classReader.accept(cv, classReaderFlags);
        return cw.toByteArray();
    }
    
    public static byte[] generateClassBytesWithSerialVersionUID(final byte[] classBytes, final int classReaderFlags, final ClassLoader classLoader) {
        final ClassReader cr = new ClassReader(classBytes);
        return generateClassBytesWithSerialVersionUID(cr, classReaderFlags, classLoader);
    }
    
    public static boolean isInterface(final ClassReader cr) {
        return (cr.getAccess() & 0x200) != 0x0;
    }
    
    private static int getClassJavaVersion(final ClassReader cr) {
        return cr.readUnsignedShort(6);
    }
    
    public static Set<Method> getDeclaredMethods(final Class<?> clazz) {
        final java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
        final Set<Method> result = new HashSet<Method>(methods.length);
        for (final java.lang.reflect.Method method : methods) {
            result.add(getMethod(method));
        }
        return result;
    }
    
    public static Method getMethod(final java.lang.reflect.Method method) {
        final Class<?>[] params = method.getParameterTypes();
        final Type[] args = new Type[params.length];
        for (int i = 0; i < params.length; ++i) {
            args[i] = Type.getType(params[i]);
        }
        return new Method(method.getName(), Type.getType(method.getReturnType()), args);
    }
}
