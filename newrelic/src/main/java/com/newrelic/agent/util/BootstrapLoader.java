// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.text.MessageFormat;
import java.lang.reflect.Field;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.newrelic.agent.util.asm.Utils;
import java.net.URL;
import java.util.logging.Level;
import com.newrelic.agent.Agent;

public abstract class BootstrapLoader
{
    private static final BootstrapLoader loader;
    
    public static BootstrapLoader get() {
        return BootstrapLoader.loader;
    }
    
    private static BootstrapLoader create() {
        try {
            return new BootstrapLoaderImpl();
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINEST, "IBM sysClassLoader property: {0}", new Object[] { System.getProperty("systemClassLoader") });
            try {
                return new IBMBootstrapLoader();
            }
            catch (Exception ex) {
                Agent.LOG.log(Level.FINEST, "IBM Bootstrap loader lookup failed: {0}", new Object[] { ex.getMessage() });
                Agent.LOG.log(Level.FINEST, (Throwable)e, "Error getting bootstrap classloader", new Object[0]);
                return new BootstrapLoader() {
                    public URL getBootstrapResource(final String name) {
                        return null;
                    }
                    
                    public boolean isBootstrapClass(final String internalName) {
                        return internalName.startsWith("java/");
                    }
                };
            }
        }
    }
    
    public boolean isBootstrapClass(final String internalName) {
        final URL bootstrapResource = this.getBootstrapResource(Utils.getClassResourceName(internalName));
        return bootstrapResource != null;
    }
    
    public abstract URL getBootstrapResource(final String p0);
    
    static {
        loader = create();
    }
    
    private static class BootstrapLoaderImpl extends BootstrapLoader
    {
        private final Method getBootstrapResourceMethod;
        
        private BootstrapLoaderImpl() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            (this.getBootstrapResourceMethod = ClassLoader.class.getDeclaredMethod("getBootstrapResource", String.class)).setAccessible(true);
            this.getBootstrapResourceMethod.invoke(null, "dummy");
        }
        
        public URL getBootstrapResource(final String name) {
            try {
                return (URL)this.getBootstrapResourceMethod.invoke(null, name);
            }
            catch (Exception e) {
                Agent.LOG.log(Level.FINEST, e.toString(), e);
                return null;
            }
        }
    }
    
    private static class IBMBootstrapLoader extends BootstrapLoader
    {
        private static final Set<String> BOOTSTRAP_CLASSLOADER_FIELDS;
        private final ClassLoader bootstrapLoader;
        
        public IBMBootstrapLoader() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
            final Field field = this.getBootstrapField();
            field.setAccessible(true);
            final ClassLoader cl = (ClassLoader)field.get(null);
            Agent.LOG.log(Level.FINEST, "Initializing IBM BootstrapLoader");
            this.bootstrapLoader = cl;
        }
        
        private Field getBootstrapField() throws NoSuchFieldException {
            for (final String fieldName : IBMBootstrapLoader.BOOTSTRAP_CLASSLOADER_FIELDS) {
                try {
                    Agent.LOG.log(Level.FINEST, "Searching for java.lang.ClassLoader.{0}", new Object[] { fieldName });
                    final Field field = ClassLoader.class.getDeclaredField(fieldName);
                    return field;
                }
                catch (NoSuchFieldException e) {}
                catch (SecurityException ex) {}
            }
            throw new NoSuchFieldException(MessageFormat.format("No bootstrap fields found: {0}", IBMBootstrapLoader.BOOTSTRAP_CLASSLOADER_FIELDS));
        }
        
        public URL getBootstrapResource(final String name) {
            return this.bootstrapLoader.getResource(name);
        }
        
        static {
            BOOTSTRAP_CLASSLOADER_FIELDS = ImmutableSet.of("bootstrapClassLoader", "systemClassLoader");
        }
    }
}
