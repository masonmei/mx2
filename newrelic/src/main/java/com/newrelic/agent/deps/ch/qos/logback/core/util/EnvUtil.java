// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

public class EnvUtil
{
    public static boolean isJDK5() {
        final String javaVersion = System.getProperty("java.version");
        return javaVersion != null && javaVersion.startsWith("1.5");
    }
    
    public static boolean isJaninoAvailable() {
        final ClassLoader classLoader = EnvUtil.class.getClassLoader();
        try {
            final Class bindingClass = classLoader.loadClass("org.codehaus.janino.ScriptEvaluator");
            return bindingClass != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static boolean isWindows() {
        final String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }
}
