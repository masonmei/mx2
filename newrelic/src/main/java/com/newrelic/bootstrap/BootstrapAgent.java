// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.bootstrap;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.net.URLClassLoader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.jar.JarFile;
import java.net.URL;

public class BootstrapAgent
{
    private static final String NEW_RELIC_JAR_FILE = "newrelic.jar";
    public static final String AGENT_CLASS_NAME = "com.newrelic.agent.Agent";
    public static final String NEW_RELIC_BOOTSTRAP_CLASSPATH = "newrelic.bootstrap_classpath";
    public static final ClassLoader AGENT_CLASSLOADER;
    private static final String WS_SERVER_JAR = "ws-server.jar";
    private static final String WS_LOG_MANAGER = "com.ibm.ws.kernel.boot.logging.WsLogManager";
    private static final String IBM_VENDOR = "IBM";
    private static long startTime;
    
    private static JarFile getAgentJarFile(final URL agentJarUrl) {
        if (agentJarUrl == null) {
            return null;
        }
        try {
            return new JarFile(URLDecoder.decode(agentJarUrl.getFile().replace("+", "%2B"), "UTF-8"));
        }
        catch (IOException e) {
            return null;
        }
    }
    
    public static URL getAgentJarUrl() {
        final ClassLoader classLoader = BootstrapAgent.class.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            final URL[] arr$;
            final URL[] urls = arr$ = ((URLClassLoader)classLoader).getURLs();
            for (final URL url : arr$) {
                if (url.getFile().endsWith("newrelic.jar")) {
                    return url;
                }
            }
            final String agentClassName = BootstrapAgent.class.getName().replace('.', '/') + ".class";
            for (final URL url2 : urls) {
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(url2.getFile());
                    final ZipEntry entry = jarFile.getEntry(agentClassName);
                    if (entry != null) {
                        return url2;
                    }
                }
                catch (IOException e) {}
                finally {
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        }
                        catch (IOException ex) {}
                    }
                }
            }
        }
        return null;
    }
    
    public static void main(final String[] args) {
        try {
            final Collection<URL> urls = BootstrapLoader.getJarURLs();
            urls.add(getAgentJarUrl());
            final ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), (ClassLoader)null);
            final Class<?> agentClass = classLoader.loadClass("com.newrelic.agent.Agent");
            final Method main = agentClass.getDeclaredMethod("main", String[].class);
            main.invoke(null, args);
        }
        catch (Throwable t) {
            System.err.println(MessageFormat.format("Error invoking the New Relic command: {0}", t));
            t.printStackTrace();
        }
    }
    
    public static void premain(final String agentArgs, final Instrumentation inst) {
        final String javaVersion = System.getProperty("java.version", "");
        if (javaVersion.startsWith("1.5")) {
            final String msg = MessageFormat.format("Java version is: {0}.  This version of the New Relic Agent does not support Java 1.5.  Please use a 2.21.x or earlier version.", javaVersion);
            System.err.println("----------");
            System.err.println(msg);
            System.err.println("----------");
            return;
        }
        checkAndApplyIBMLibertyProfileLogManagerWorkaround();
        startAgent(agentArgs, inst);
    }
    
    private static void checkAndApplyIBMLibertyProfileLogManagerWorkaround() {
        final String javaVendor = System.getProperty("java.vendor");
        if (javaVendor != null && javaVendor.startsWith("IBM")) {
            final String javaClassPath = System.getProperty("java.class.path");
            if (javaClassPath != null && javaClassPath.contains("ws-server.jar") && System.getProperty("java.util.logging.manager") == null) {
                try {
                    Class.forName("com.ibm.ws.kernel.boot.logging.WsLogManager", false, ClassLoader.getSystemClassLoader());
                    System.setProperty("java.util.logging.manager", "com.ibm.ws.kernel.boot.logging.WsLogManager");
                }
                catch (Exception ex) {}
            }
        }
    }
    
    static void startAgent(final String agentArgs, final Instrumentation inst) {
        if (isBootstrapClasspathFlagSet()) {
            Class<?> clazz = BootstrapLoader.class;
            clazz = BootstrapLoader.ApiClassTransformer.class;
            appendJarToBootstrapClassLoader(inst);
        }
        try {
            BootstrapAgent.startTime = System.currentTimeMillis();
            BootstrapLoader.load(inst);
            final Class<?> agentClass = ClassLoader.getSystemClassLoader().loadClass("com.newrelic.agent.Agent");
            final Method premain = agentClass.getDeclaredMethod("premain", String.class, Instrumentation.class);
            premain.invoke(null, agentArgs, inst);
        }
        catch (Throwable t) {
            System.err.println(MessageFormat.format("Error bootstrapping New Relic agent: {0}", t));
            t.printStackTrace();
        }
    }
    
    public static boolean isBootstrapClasspathFlagSet() {
        return Boolean.getBoolean("newrelic.bootstrap_classpath");
    }
    
    public static long getAgentStartTime() {
        return BootstrapAgent.startTime;
    }
    
    private static void appendJarToBootstrapClassLoader(final Instrumentation inst) {
        final URL agentJarUrl = getAgentJarUrl();
        final JarFile agentJarFile = getAgentJarFile(agentJarUrl);
        if (agentJarFile != null) {
            inst.appendToBootstrapClassLoaderSearch(agentJarFile);
        }
    }
    
    static {
        AGENT_CLASSLOADER = BootstrapAgent.class.getClassLoader();
    }
}
