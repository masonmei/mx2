// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.bootstrap;

import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import com.newrelic.agent.deps.com.google.common.cache.CacheLoader;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import java.io.File;
import com.newrelic.agent.deps.com.google.common.cache.LoadingCache;

public class EmbeddedJarFilesImpl implements EmbeddedJarFiles
{
    private static final String[] INTERNAL_JAR_FILE_NAMES;
    public static final EmbeddedJarFiles INSTANCE;
    private final LoadingCache<String, File> embeddedAgentJarFiles;
    private final String[] jarFileNames;
    
    public EmbeddedJarFilesImpl() {
        this(EmbeddedJarFilesImpl.INTERNAL_JAR_FILE_NAMES);
    }
    
    public EmbeddedJarFilesImpl(final String[] jarFileNames) {
        this.embeddedAgentJarFiles = CacheBuilder.newBuilder().build((CacheLoader<? super String, File>)new CacheLoader<String, File>() {
            public File load(final String jarNameWithoutExtension) throws IOException {
                final InputStream jarStream = ClassLoader.getSystemClassLoader().getResourceAsStream(jarNameWithoutExtension + ".jar");
                if (jarStream == null) {
                    throw new FileNotFoundException(jarNameWithoutExtension + ".jar");
                }
                final File file = File.createTempFile(jarNameWithoutExtension, ".jar");
                file.deleteOnExit();
                final OutputStream out = new FileOutputStream(file);
                try {
                    BootstrapLoader.copy(jarStream, out, 8096, true);
                    return file;
                }
                finally {
                    out.close();
                }
            }
        });
        this.jarFileNames = jarFileNames;
    }
    
    public File getJarFileInAgent(final String jarNameWithoutExtension) throws IOException {
        try {
            return this.embeddedAgentJarFiles.get(jarNameWithoutExtension);
        }
        catch (ExecutionException e) {
            try {
                throw e.getCause();
            }
            catch (IOException ex) {
                throw ex;
            }
            catch (Throwable ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }
    
    public String[] getEmbeddedAgentJarFileNames() {
        return this.jarFileNames;
    }
    
    static {
        INTERNAL_JAR_FILE_NAMES = new String[] { "agent-bridge", "newrelic-api", "newrelic-weaver-api" };
        INSTANCE = new EmbeddedJarFilesImpl();
    }
}
