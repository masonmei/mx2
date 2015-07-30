// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.lang.reflect.Method;

public class Invoker
{
    public static Object invoke(final Object called, final Class<?> clazz, final String methodName, final Object... args) throws Exception {
        final Class<?>[] argTypes = (Class<?>[])new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            argTypes[i] = args[i].getClass();
        }
        return invoke(called, clazz, methodName, argTypes, args);
    }
    
    private static Object invoke(final Object called, final Class<?> clazz, final String methodName, final Class<?>[] argTypes, final Object[] args) throws Exception {
        final Method method = clazz.getMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(called, args);
    }
    
    public static String getClassNameFromInternalName(final String className) {
        return Type.getObjectType(className).getClassName();
    }
}
