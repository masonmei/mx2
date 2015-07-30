// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.util.HashMap;
import java.util.Map;

public enum NonUrlClassLoaders
{
    JBOSS_6(new String[] { "org.jboss.classloader.spi.base.BaseClassLoader" }), 
    JBOSS_7(new String[] { "org.jboss.modules.ModuleClassLoader" }), 
    WEBSHPERE_8(new String[] { "com.ibm.ws.classloader.CompoundClassLoader" }), 
    WEBLOGIC(new String[] { "weblogic.utils.classloaders.GenericClassLoader", "weblogic.utils.classloaders.ChangeAwareClassLoader" });
    
    private String[] classLoaderNames;
    private static Map<String, NonUrlClassLoaders> LOADERS;
    
    private NonUrlClassLoaders(final String[] classNames) {
        this.classLoaderNames = classNames;
    }
    
    public static NonUrlClassLoaders getNonUrlType(final String loaderCanonicalName) {
        if (loaderCanonicalName != null) {
            return NonUrlClassLoaders.LOADERS.get(loaderCanonicalName);
        }
        return null;
    }
    
    static {
        NonUrlClassLoaders.LOADERS = new HashMap<String, NonUrlClassLoaders>();
        for (final NonUrlClassLoaders classLoader : values()) {
            final String[] arr$2;
            final String[] classes = arr$2 = classLoader.classLoaderNames;
            for (final String current : arr$2) {
                NonUrlClassLoaders.LOADERS.put(current, classLoader);
            }
        }
    }
}
