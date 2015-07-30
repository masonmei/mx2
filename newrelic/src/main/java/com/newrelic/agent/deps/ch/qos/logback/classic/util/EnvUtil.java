// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

public class EnvUtil
{
    public static boolean isGroovyAvailable() {
        final ClassLoader classLoader = EnvUtil.class.getClassLoader();
        try {
            final Class bindingClass = classLoader.loadClass("groovy.lang.Binding");
            return bindingClass != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}
