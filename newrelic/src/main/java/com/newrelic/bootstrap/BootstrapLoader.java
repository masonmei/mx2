// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.bootstrap;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.jar.Manifest;
import java.util.jar.JarOutputStream;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.io.OutputStream;
import com.newrelic.agent.util.Streams;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.regex.Pattern;
import com.newrelic.agent.config.JarResource;
import com.newrelic.agent.config.AgentJarHelper;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.lang.instrument.ClassFileTransformer;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.jar.JarFile;
import java.lang.instrument.Instrumentation;

public class BootstrapLoader
{
    public static final String AGENT_BRIDGE_JAR_NAME = "agent-bridge";
    public static final String API_JAR_NAME = "newrelic-api";
    public static final String WEAVER_API_JAR_NAME = "newrelic-weaver-api";
    private static final String NEWRELIC_BOOTSTRAP = "newrelic-bootstrap";
    private static final String NEWRELIC_API_INTERNAL_CLASS_NAME = "com/newrelic/api/agent/NewRelic";
    
    private static void addBridgeJarToClassPath(final Instrumentation instrProxy) throws ClassNotFoundException, IOException {
        final JarFile jarFileInAgent = new JarFile(EmbeddedJarFilesImpl.INSTANCE.getJarFileInAgent("agent-bridge"));
        forceCorrectNewRelicApi(instrProxy, jarFileInAgent);
        addJarToClassPath(instrProxy, jarFileInAgent);
    }
    
    private static void forceCorrectNewRelicApi(final Instrumentation instrProxy, final JarFile bridgeJarFile) throws IOException {
        final JarEntry jarEntry = bridgeJarFile.getJarEntry("com/newrelic/api/agent/NewRelic.class");
        final byte[] bytes = read(bridgeJarFile.getInputStream(jarEntry), true);
        instrProxy.addTransformer(new ApiClassTransformer(bytes), true);
    }
    
    private static void addJarToClassPath(final Instrumentation instrProxy, final JarFile jarfile) {
        instrProxy.appendToBootstrapClassLoaderSearch(jarfile);
    }
    
    public static Collection<URL> getJarURLs() throws ClassNotFoundException, IOException {
        final List<URL> urls = new ArrayList<URL>();
        for (final String name : new String[] { "agent-bridge", "newrelic-api", "newrelic-weaver-api" }) {
            final File jarFileInAgent = EmbeddedJarFilesImpl.INSTANCE.getJarFileInAgent(name);
            urls.add(jarFileInAgent.toURI().toURL());
        }
        return urls;
    }
    
    static void load(final Instrumentation inst) {
        try {
            addMixinInterfacesToBootstrap(inst);
            addBridgeJarToClassPath(inst);
            addJarToClassPath(inst, new JarFile(EmbeddedJarFilesImpl.INSTANCE.getJarFileInAgent("newrelic-api")));
            addJarToClassPath(inst, new JarFile(EmbeddedJarFilesImpl.INSTANCE.getJarFileInAgent("newrelic-weaver-api")));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void addMixinInterfacesToBootstrap(final Instrumentation inst) {
        if (isDisableMixinsOnBootstrap()) {
            System.out.println("New Relic Agent: mixin interfaces not moved to bootstrap");
            return;
        }
        JarResource agentJarResource = null;
        try {
            agentJarResource = AgentJarHelper.getAgentJarResource();
            final URL agentJarUrl = AgentJarHelper.getAgentJarUrl();
            addMixinInterfacesToBootstrap(agentJarResource, agentJarUrl, inst);
        }
        finally {
            try {
                agentJarResource.close();
            }
            catch (Throwable th) {
                logIfNRDebug("closing Agent jar resource", th);
            }
        }
    }
    
    public static void addMixinInterfacesToBootstrap(final JarResource agentJarResource, final URL agentJarUrl, final Instrumentation inst) {
        boolean succeeded = false;
        final Pattern packageSearchPattern = Pattern.compile("com/newrelic/agent/instrumentation/pointcuts/(.*).class");
        final String interfaceMixinAnnotation = "Lcom/newrelic/agent/instrumentation/pointcuts/InterfaceMixin;";
        final String loadOnBootstrapAnnotation = "Lcom/newrelic/agent/instrumentation/pointcuts/LoadOnBootstrap;";
        final String interfaceMapperAnnotation = "Lcom/newrelic/agent/instrumentation/pointcuts/InterfaceMapper;";
        final String methodMapperAnnotation = "Lcom/newrelic/agent/instrumentation/pointcuts/MethodMapper;";
        final String fieldAccessorAnnotation = "Lcom/newrelic/agent/instrumentation/pointcuts/FieldAccessor;";
        final List<String> bootstrapAnnotations = Arrays.asList("Lcom/newrelic/agent/instrumentation/pointcuts/InterfaceMixin;", "Lcom/newrelic/agent/instrumentation/pointcuts/InterfaceMapper;", "Lcom/newrelic/agent/instrumentation/pointcuts/MethodMapper;", "Lcom/newrelic/agent/instrumentation/pointcuts/FieldAccessor;", "Lcom/newrelic/agent/instrumentation/pointcuts/LoadOnBootstrap;");
        File generatedFile = null;
        JarOutputStream outputJarStream = null;
        try {
            generatedFile = File.createTempFile("newrelic-bootstrap", ".jar");
            final Manifest manifest = createManifest();
            outputJarStream = createJarOutputStream(generatedFile, manifest);
            final long modTime = System.currentTimeMillis();
            final Collection<String> fileNames = AgentJarHelper.findJarFileNames(agentJarUrl, packageSearchPattern);
            for (final String fileName : fileNames) {
                final int size = (int)agentJarResource.getSize(fileName);
                final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
                Streams.copy(agentJarResource.getInputStream(fileName), out, size, true);
                final byte[] classBytes = out.toByteArray();
                final ClassReader cr = new ClassReader(classBytes);
                final ClassStructure structure = ClassStructure.getClassStructure(cr, 4);
                final Collection<String> annotations = structure.getClassAnnotations().keySet();
                if (containsAnyOf(bootstrapAnnotations, annotations)) {
                    final JarEntry entry = new JarEntry(fileName);
                    entry.setTime(modTime);
                    outputJarStream.putNextEntry(entry);
                    outputJarStream.write(classBytes);
                }
            }
            outputJarStream.closeEntry();
            succeeded = true;
        }
        catch (IOException iox) {
            logIfNRDebug("generating mixin jar file", iox);
            try {
                outputJarStream.close();
            }
            catch (Throwable th) {
                logIfNRDebug("closing outputJarStream", th);
            }
        }
        finally {
            try {
                outputJarStream.close();
            }
            catch (Throwable th2) {
                logIfNRDebug("closing outputJarStream", th2);
            }
        }
        if (succeeded) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(generatedFile);
                inst.appendToBootstrapClassLoaderSearch(jarFile);
                generatedFile.deleteOnExit();
            }
            catch (IOException iox2) {
                logIfNRDebug("adding dynamic mixin jar to bootstrap", iox2);
                try {
                    jarFile.close();
                }
                catch (Throwable th3) {
                    logIfNRDebug("closing generated jar file", th3);
                }
            }
            finally {
                try {
                    jarFile.close();
                }
                catch (Throwable th4) {
                    logIfNRDebug("closing generated jar file", th4);
                }
            }
        }
    }
    
    private static final boolean containsAnyOf(final Collection<?> searchFor, final Collection<?> searchIn) {
        for (final Object key : searchFor) {
            if (searchIn.contains(key)) {
                return true;
            }
        }
        return false;
    }
    
    private static final boolean isNewRelicDebug() {
        final String newrelicDebug = "newrelic.debug";
        return System.getProperty("newrelic.debug") != null && Boolean.getBoolean("newrelic.debug");
    }
    
    private static final boolean isDisableMixinsOnBootstrap() {
        final String newrelicDisableMixinsOnBootstrap = "newrelic.disable.mixins.on.bootstrap";
        return System.getProperty(newrelicDisableMixinsOnBootstrap) != null && Boolean.getBoolean(newrelicDisableMixinsOnBootstrap);
    }
    
    private static final void logIfNRDebug(final String msg, final Throwable th) {
        if (isNewRelicDebug()) {
            System.out.println("While bootstrapping the Agent: " + msg + ": " + th.getStackTrace());
        }
    }
    
    private static final JarOutputStream createJarOutputStream(final File jarFile, final Manifest manifest) throws IOException {
        final FileOutputStream outStream = new FileOutputStream(jarFile);
        return new JarOutputStream(outStream, manifest);
    }
    
    private static final Manifest createManifest() {
        final Manifest manifest = new Manifest();
        final Attributes a = manifest.getMainAttributes();
        a.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        a.put(Attributes.Name.IMPLEMENTATION_TITLE, "Interface Mixins");
        a.put(Attributes.Name.IMPLEMENTATION_VERSION, "1.0");
        a.put(Attributes.Name.IMPLEMENTATION_VENDOR, "New Relic");
        return manifest;
    }
    
    static int copy(final InputStream input, final OutputStream output, final int bufferSize, final boolean closeStreams) throws IOException {
        try {
            final byte[] buffer = new byte[bufferSize];
            int count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }
        finally {
            if (closeStreams) {
                input.close();
                output.close();
            }
        }
    }
    
    static byte[] read(final InputStream input, final boolean closeInputStream) throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        copy(input, outStream, input.available(), closeInputStream);
        return outStream.toByteArray();
    }
    
    static final class ApiClassTransformer implements ClassFileTransformer
    {
        private final byte[] bytes;
        
        ApiClassTransformer(final byte[] bytes) {
            this.bytes = bytes;
        }
        
        public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
            if ("com/newrelic/api/agent/NewRelic".equals(className)) {
                return this.bytes;
            }
            return null;
        }
    }
}
