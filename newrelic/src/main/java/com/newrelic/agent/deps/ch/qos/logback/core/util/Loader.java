// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import java.security.AccessControlException;
import java.security.Permission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.net.URL;
import java.util.Set;

public class Loader
{
    static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
    private static boolean ignoreTCL;
    public static final String IGNORE_TCL_PROPERTY_NAME = "logback.ignoreTCL";
    private static boolean HAS_GET_CLASS_LOADER_PERMISSION;
    
    public static Set<URL> getResourceOccurrenceCount(final String resource, final ClassLoader classLoader) throws IOException {
        final Set<URL> urlSet = new HashSet<URL>();
        final Enumeration<URL> urlEnum = classLoader.getResources(resource);
        while (urlEnum.hasMoreElements()) {
            final URL url = urlEnum.nextElement();
            urlSet.add(url);
        }
        return urlSet;
    }
    
    public static URL getResource(final String resource, final ClassLoader classLoader) {
        try {
            return classLoader.getResource(resource);
        }
        catch (Throwable t) {
            return null;
        }
    }
    
    public static URL getResourceBySelfClassLoader(final String resource) {
        return getResource(resource, getClassLoaderOfClass(Loader.class));
    }
    
    public static ClassLoader getTCL() {
        return Thread.currentThread().getContextClassLoader();
    }
    
    public static Class loadClass(final String clazz, final Context context) throws ClassNotFoundException {
        final ClassLoader cl = getClassLoaderOfObject(context);
        return cl.loadClass(clazz);
    }
    
    public static ClassLoader getClassLoaderOfObject(final Object o) {
        if (o == null) {
            throw new NullPointerException("Argument cannot be null");
        }
        return getClassLoaderOfClass(o.getClass());
    }
    
    public static ClassLoader getClassLoaderAsPrivileged(final Class clazz) {
        if (!Loader.HAS_GET_CLASS_LOADER_PERMISSION) {
            return null;
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        });
    }
    
    public static ClassLoader getClassLoaderOfClass(final Class clazz) {
        final ClassLoader cl = clazz.getClassLoader();
        if (cl == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return cl;
    }
    
    public static Class loadClass(final String clazz) throws ClassNotFoundException {
        if (Loader.ignoreTCL) {
            return Class.forName(clazz);
        }
        try {
            return getTCL().loadClass(clazz);
        }
        catch (Throwable e) {
            return Class.forName(clazz);
        }
    }
    
    static {
        Loader.ignoreTCL = false;
        Loader.HAS_GET_CLASS_LOADER_PERMISSION = false;
        final String ignoreTCLProp = OptionHelper.getSystemProperty("logback.ignoreTCL", null);
        if (ignoreTCLProp != null) {
            Loader.ignoreTCL = OptionHelper.toBoolean(ignoreTCLProp, true);
        }
        Loader.HAS_GET_CLASS_LOADER_PERMISSION = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            public Boolean run() {
                try {
                    AccessController.checkPermission(new RuntimePermission("getClassLoader"));
                    return true;
                }
                catch (AccessControlException e) {
                    return false;
                }
            }
        });
    }
}
