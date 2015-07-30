// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.net.URLDecoder;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.Collections;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.ArrayList;
import java.net.URL;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Collection;
import java.util.regex.Pattern;

public class AgentJarHelper
{
    private static final Pattern AGENT_CLASS_PATTERN;
    private static final String AGENT_CLASS_NAME;
    private static final String NEW_RELIC_JAR_FILE = "newrelic.jar";
    private static final String BUILT_DATE_ATTRIBUTE = "Built-Date";
    
    public static Collection<String> findAgentJarFileNames(final Pattern pattern) {
        final URL agentJarUrl = getAgentJarUrl();
        Agent.LOG.log(Level.FINEST, "Searching for " + pattern.pattern() + " in " + agentJarUrl.getPath());
        return findJarFileNames(agentJarUrl, pattern);
    }
    
    public static Collection<String> findJarFileNames(final URL agentJarUrl, final Pattern pattern) {
        JarFile jarFile = null;
        try {
            jarFile = getAgentJarFile(agentJarUrl);
            final Collection<String> names = new ArrayList<String>();
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (pattern.matcher(jarEntry.getName()).matches()) {
                    names.add(jarEntry.getName());
                }
            }
            final Collection<String> collection = names;
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException ex) {}
            }
            return collection;
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINEST, "Unable to search the agent jar for " + pattern.pattern(), e);
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException ex2) {}
            }
        }
        finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException ex3) {}
            }
        }
        return (Collection<String>)Collections.emptyList();
    }
    
    public static boolean jarFileNameExists(final URL agentJarUrl, final String name) {
        JarFile jarFile = null;
        try {
            jarFile = getAgentJarFile(agentJarUrl);
            final boolean b = jarFile.getEntry(name) != null;
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException ex) {}
            }
            return b;
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINEST, "Unable to search the agent jar for " + name, e);
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException ex2) {}
            }
        }
        finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (IOException ex3) {}
            }
        }
        return false;
    }
    
    public static File getAgentJarDirectory() {
        final URL agentJarUrl = getAgentJarUrl();
        if (agentJarUrl != null) {
            final File file = new File(getAgentJarFileName(agentJarUrl));
            if (file.exists()) {
                return file.getParentFile();
            }
        }
        return null;
    }
    
    public static URL getAgentJarUrl() {
        if (System.getProperty("newrelic.agent_jarfile") != null) {
            try {
                return new URL("file://" + System.getProperty("newrelic.agent_jarfile"));
            }
            catch (MalformedURLException e) {
                Agent.LOG.log(Level.FINEST, "Unable to create a valid url from " + System.getProperty("newrelic.agent_jarfile"), e);
            }
        }
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        if (classLoader instanceof URLClassLoader) {
            final URL[] arr$;
            final URL[] urls = arr$ = ((URLClassLoader)classLoader).getURLs();
            for (final URL url : arr$) {
                if (url.getFile().endsWith("newrelic.jar") && jarFileNameExists(url, AgentJarHelper.AGENT_CLASS_NAME)) {
                    return url;
                }
            }
            final String agentClassName = Agent.class.getName().replace('.', '/') + ".class";
            for (final URL url2 : urls) {
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(url2.getFile());
                    final ZipEntry entry = jarFile.getEntry(agentClassName);
                    if (entry != null) {
                        final URL url3 = url2;
                        if (jarFile != null) {
                            try {
                                jarFile.close();
                            }
                            catch (IOException ex) {}
                        }
                        return url3;
                    }
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        }
                        catch (IOException ex2) {}
                    }
                }
                catch (IOException e2) {
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        }
                        catch (IOException ex3) {}
                    }
                }
                finally {
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        }
                        catch (IOException ex4) {}
                    }
                }
            }
        }
        return AgentJarHelper.class.getProtectionDomain().getCodeSource().getLocation();
    }
    
    public static JarResource getAgentJarResource() {
        final JarFile agentJarFile = getAgentJarFile();
        if (agentJarFile == null) {
            return new JarResource() {
                public void close() throws IOException {
                }
                
                public InputStream getInputStream(final String name) {
                    return AgentJarHelper.class.getResourceAsStream('/' + name);
                }
                
                public long getSize(final String name) {
                    return 128L;
                }
            };
        }
        return new JarResource() {
            public void close() throws IOException {
                agentJarFile.close();
            }
            
            public InputStream getInputStream(final String name) throws IOException {
                final ZipEntry entry = agentJarFile.getJarEntry(name);
                return agentJarFile.getInputStream(entry);
            }
            
            public long getSize(final String name) {
                final ZipEntry entry = agentJarFile.getJarEntry(name);
                return entry.getSize();
            }
        };
    }
    
    private static JarFile getAgentJarFile() {
        final URL agentJarUrl = getAgentJarUrl();
        return getAgentJarFile(agentJarUrl);
    }
    
    private static JarFile getAgentJarFile(final URL agentJarUrl) {
        if (agentJarUrl == null) {
            return null;
        }
        try {
            return new JarFile(getAgentJarFileName(agentJarUrl));
        }
        catch (IOException e) {
            return null;
        }
    }
    
    private static String getAgentJarFileName(final URL agentJarUrl) {
        if (agentJarUrl == null) {
            return null;
        }
        try {
            return URLDecoder.decode(agentJarUrl.getFile().replace("+", "%2B"), "UTF-8");
        }
        catch (IOException e) {
            return null;
        }
    }
    
    public static String getAgentJarFileName() {
        final URL agentJarUrl = getAgentJarUrl();
        return getAgentJarFileName(agentJarUrl);
    }
    
    public static String getBuildDate() {
        return getAgentJarAttribute("Built-Date");
    }
    
    public static String getAgentJarAttribute(final String name) {
        final JarFile jarFile = getAgentJarFile();
        if (jarFile == null) {
            return null;
        }
        try {
            return jarFile.getManifest().getMainAttributes().getValue(name);
        }
        catch (IOException e) {
            return null;
        }
    }
    
    static {
        AGENT_CLASS_PATTERN = Pattern.compile(Type.getInternalName(Agent.class) + ".class");
        AGENT_CLASS_NAME = Type.getInternalName(Agent.class) + ".class";
    }
}
