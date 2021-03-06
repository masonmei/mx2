// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import com.newrelic.api.agent.NewRelic;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ClassReflection
{
    public static ClassLoader getClassLoader(final Class<?> clazz) {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        });
    }
    
    public static Class<?> loadClass(final ClassLoader classLoader, final String name) throws ClassNotFoundException {
        NewRelic.getAgent().getLogger().log(Level.FINEST, "Loading class {0}", new Object[] { name });
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>)new PrivilegedExceptionAction<Class<?>>() {
                public Class<?> run() throws Exception {
                    return classLoader.loadClass(name);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw new ClassNotFoundException("Unable to load " + name, e);
        }
    }
    
    public static Method[] getDeclaredMethods(final Class<?> clazz) {
        return clazz.getDeclaredMethods();
    }
    
    public static Method getDeclaredMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        final Method m = clazz.getDeclaredMethod(name, parameterTypes);
        return m;
    }
    
    public static Method[] getMethods(final Class<?> clazz) {
        return clazz.getMethods();
    }
    
    public static Constructor<?>[] getDeclaredConstructors(final Class<?> clazz) {
        return clazz.getDeclaredConstructors();
    }
    
    public static Field[] getDeclaredFields(final Class<?> clazz) {
        return clazz.getDeclaredFields();
    }
    
    public static Field getDeclaredField(final Class<?> clazz, final String name) throws NoSuchFieldException, SecurityException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Field>)new PrivilegedExceptionAction<Field>() {
                public Field run() throws Exception {
                    final Field field = clazz.getDeclaredField(name);
                    field.setAccessible(true);
                    return field;
                }
            });
        }
        catch (PrivilegedActionException e) {
            try {
                throw e.getCause();
            }
            catch (NoSuchFieldException ex) {
                throw ex;
            }
            catch (SecurityException ex2) {
                throw ex2;
            }
            catch (Throwable t) {
                throw new SecurityException(e);
            }
        }
    }
    
    public static Object get(final Field field, final Object instance) throws IllegalArgumentException, IllegalAccessException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    return field.get(instance);
                }
            });
        }
        catch (PrivilegedActionException e) {
            return handleAccessException(e.getCause());
        }
    }
    
    private static Void handleAccessException(final Throwable cause) throws IllegalAccessException, IllegalArgumentException {
        try {
            throw cause;
        }
        catch (IllegalAccessException ex) {
            throw ex;
        }
        catch (IllegalArgumentException ex2) {
            throw ex2;
        }
        catch (Throwable t) {
            throw new IllegalAccessException(t.toString());
        }
    }
    
    public static void setAccessible(final Field field, final boolean flag) throws IllegalArgumentException, IllegalAccessException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    field.setAccessible(flag);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException e) {
            handleAccessException(e.getCause());
        }
    }
    
    public static void setAccessible(final Method method, final boolean flag) throws IllegalArgumentException, IllegalAccessException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    method.setAccessible(flag);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException e) {
            handleAccessException(e.getCause());
        }
    }
}
