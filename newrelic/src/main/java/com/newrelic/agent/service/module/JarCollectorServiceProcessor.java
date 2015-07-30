// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.module;

import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;
import java.util.jar.JarInputStream;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Collection;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import com.newrelic.agent.service.ServiceFactory;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.List;

class JarCollectorServiceProcessor
{
    private static final String SHA1_CHECKSUM_KEY = "sha1Checksum";
    static final String JAR_EXTENSION = ".jar";
    static final String JAR_PROTOCOL = "jar";
    static final String UNKNOWN_VERSION = " ";
    private static final int MAX_MAP_SIZE = 1000;
    private static final JarInfo NON_JAR;
    private static final JarInfo JAR_ERROR;
    private final boolean skipTempJars;
    private final List<String> ignoreJars;
    private final Map<URL, JarInfo> sentJars;
    private static final File TEMP_DIRECTORY;
    
    public JarCollectorServiceProcessor() {
        this(ServiceFactory.getConfigService().getDefaultAgentConfig().getIgnoreJars());
    }
    
    JarCollectorServiceProcessor(final List<String> ignoreJars) {
        this.ignoreJars = ignoreJars;
        this.sentJars = CacheBuilder.newBuilder().maximumSize(1000L).weakKeys().<URL, JarInfo>build().asMap();
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        if (!(this.skipTempJars = (Boolean)config.getValue("jar_collector.skip_temp_jars", (Object)true))) {
            Agent.LOG.finest("Jar collector: temporary jars will be transmitted to the host");
        }
    }
    
    protected synchronized List<Jar> processModuleData(Collection<URL> urlsToProcess, final boolean sendAll) {
        urlsToProcess = Sets.newHashSet(urlsToProcess);
        final List<Jar> jars = Lists.newArrayList();
        if (sendAll) {
            urlsToProcess.addAll(this.sentJars.keySet());
        }
        else {
            urlsToProcess.removeAll(this.sentJars.keySet());
        }
        final Map<URL, JarInfo> processedUrls = this.processUrls(urlsToProcess, jars);
        this.sentJars.putAll(processedUrls);
        return jars;
    }
    
    private Map<URL, JarInfo> processUrls(final Collection<URL> urls, final List<Jar> jars) {
        final Map<URL, JarInfo> jarDetails = Maps.newHashMap();
        for (final URL address : urls) {
            JarInfo jar = JarCollectorServiceProcessor.NON_JAR;
            try {
                if (this.skipTempJars && isTempFile(address)) {
                    Agent.LOG.log(Level.FINE, "Skipping temp jar file {0}", new Object[] { address.toString() });
                }
                else {
                    Agent.LOG.log(Level.FINEST, "Processing jar file {0}", new Object[] { address.toString() });
                    jar = this.processUrl(address, jars);
                }
            }
            catch (Exception e) {
                Agent.LOG.log(Level.FINEST, "While processing {0}: {1}: {2}", new Object[] { address, e.getClass().getSimpleName(), e.getMessage() });
            }
            jarDetails.put(address, jar);
        }
        return jarDetails;
    }
    
    static boolean isTempFile(final URL address) throws URISyntaxException {
        return "file".equals(address.getProtocol()) && isTempFile(new File(address.toURI()));
    }
    
    static boolean isTempFile(File file) {
        file = file.getParentFile();
        return null != file && (JarCollectorServiceProcessor.TEMP_DIRECTORY.equals(file) || isTempFile(file));
    }
    
    private JarInfo processUrl(final URL url, final List<Jar> jars) {
        try {
            if (!url.getFile().endsWith(".jar")) {
                return JarCollectorServiceProcessor.NON_JAR;
            }
            Agent.LOG.log(Level.FINEST, "URL has file path {0}.", new Object[] { url.getFile() });
            return this.handleJar(url, jars);
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINEST, (Throwable)e, "Error processing the file path : {0}", new Object[] { e.getMessage() });
            return JarCollectorServiceProcessor.JAR_ERROR;
        }
    }
    
    private JarInfo handleJar(final URL url, final List<Jar> jars) {
        final JarInfo jarInfo = getJarInfoSafe(url);
        this.addJarAndVersion(url, jarInfo, jars);
        return jarInfo;
    }
    
    static JarInfo getJarInfoSafe(final URL url) {
        String sha1Checksum = "UNKNOWN";
        try {
            sha1Checksum = ShaChecksums.computeSha(url);
        }
        catch (Exception ex) {
            Agent.LOG.log(Level.FINE, "Error getting jar file checksum : {0}", new Object[] { ex.getMessage() });
            Agent.LOG.log(Level.FINEST, (Throwable)ex, "{0}", new Object[] { ex.getMessage() });
        }
        JarInfo jarInfo;
        try {
            jarInfo = getJarInfo(url, sha1Checksum);
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINEST, (Throwable)e, "Trouble getting version from {0} jar. Adding jar without version.", new Object[] { url.getFile() });
            jarInfo = new JarInfo(" ", ImmutableMap.of("sha1Checksum", sha1Checksum));
        }
        return jarInfo;
    }
    
    private static JarInfo getJarInfo(final URL url, final String sha1Checksum) throws IOException {
        final Map<String, String> attributes = Maps.newHashMap();
        attributes.put("sha1Checksum", sha1Checksum);
        final JarInputStream jarFile = EmbeddedJars.getJarInputStream(url);
        try {
            try {
                Map pom = getPom(jarFile);
                if (pom != null) {
                    attributes.putAll(pom);
                    final JarInfo jarInfo = new JarInfo((String)pom.get("version"), attributes);
                    jarFile.close();
                    return jarInfo;
                }
            }
            catch (Exception ex) {
                Agent.LOG.log(Level.FINEST, ex, "{0}", new Object[] { ex.getMessage() });
            }
            String version = getVersion(jarFile);
            if (version == null) {
                version = " ";
            }
            final JarInfo jarInfo2 = new JarInfo(version, attributes);
            jarFile.close();
            return jarInfo2;
        }
        finally {
            jarFile.close();
        }
    }
    
    private static Map<Object, Object> getPom(final JarInputStream jarFile) throws IOException {
        Map<Object, Object> pom = null;
        JarEntry entry = null;
        while ((entry = jarFile.getNextJarEntry()) != null) {
            if (entry.getName().startsWith("META-INF/maven") && entry.getName().endsWith("pom.properties")) {
                if (pom != null) {
                    return null;
                }
                final Properties props = new Properties();
                props.load(jarFile);
                pom = props;
            }
        }
        return pom;
    }
    
    static String getVersion(final JarInputStream jarFile) {
        final Manifest manifest = jarFile.getManifest();
        if (manifest == null) {
            return null;
        }
        String version = getVersion(manifest.getMainAttributes());
        if (version == null && !manifest.getEntries().isEmpty()) {
            version = getVersion(manifest.getEntries().values().iterator().next());
        }
        return version;
    }
    
    private static String getVersion(final Attributes attributes) {
        String version = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        if (version == null) {
            version = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            if (version == null) {
                version = attributes.getValue("Bundle-Version");
            }
        }
        return version;
    }
    
    boolean addJarAndVersion(final URL url, JarInfo jarInfo, final List<Jar> jars) {
        if (jarInfo == null) {
            jarInfo = JarInfo.MISSING;
        }
        boolean added = false;
        String jarFile = null;
        try {
            jarFile = parseJarName(url);
            if (this.shouldAttemptAdd(jarFile)) {
                jars.add(new Jar(jarFile, jarInfo));
                added = true;
            }
        }
        catch (URISyntaxException e) {
            Agent.LOG.log(Level.FINEST, (Throwable)e, "{0}", new Object[] { e.getMessage() });
        }
        if (added) {
            Agent.LOG.log(Level.FINER, "Adding the jar {0} with version {1}.", new Object[] { jarFile, jarInfo.version });
        }
        else {
            Agent.LOG.log(Level.FINER, "Not taking version {0} for jar {1}.", new Object[] { jarInfo.version, jarFile });
        }
        return added;
    }
    
    static String parseJarName(final URL url) throws URISyntaxException {
        if ("file".equals(url.getProtocol())) {
            final File file = new File(url.toURI());
            return file.getName().trim();
        }
        Agent.LOG.log(Level.FINEST, "Parsing jar file name from {0}", new Object[] { url });
        String path = url.getFile();
        final int end = path.lastIndexOf(".jar");
        if (end > 0) {
            path = path.substring(0, end);
            final int start = path.lastIndexOf(File.separator);
            if (start > 0) {
                return path.substring(start + 1) + ".jar";
            }
        }
        throw new URISyntaxException(url.getPath(), "Unable to parse the jar file name from a URL");
    }
    
    private boolean shouldAttemptAdd(final String jarFile) {
        return !this.ignoreJars.contains(jarFile);
    }
    
    static {
        NON_JAR = new JarInfo(null, null);
        JAR_ERROR = new JarInfo(null, null);
        TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    }
}
