// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.io;

import java.io.InputStream;
import com.newrelic.agent.deps.com.google.common.base.Preconditions;
import com.newrelic.agent.deps.com.google.common.base.MoreObjects;
import java.io.OutputStream;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;
import java.io.IOException;
import java.nio.charset.Charset;
import java.net.URL;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public final class Resources
{
    public static ByteSource asByteSource(final URL url) {
        return new UrlByteSource(url);
    }
    
    public static CharSource asCharSource(final URL url, final Charset charset) {
        return asByteSource(url).asCharSource(charset);
    }
    
    public static byte[] toByteArray(final URL url) throws IOException {
        return asByteSource(url).read();
    }
    
    public static String toString(final URL url, final Charset charset) throws IOException {
        return asCharSource(url, charset).read();
    }
    
    public static <T> T readLines(final URL url, final Charset charset, final LineProcessor<T> callback) throws IOException {
        return asCharSource(url, charset).readLines(callback);
    }
    
    public static List<String> readLines(final URL url, final Charset charset) throws IOException {
        return readLines(url, charset, (LineProcessor<List<String>>)new LineProcessor<List<String>>() {
            final List<String> result = Lists.newArrayList();
            
            @Override
            public boolean processLine(final String line) {
                this.result.add(line);
                return true;
            }
            
            @Override
            public List<String> getResult() {
                return this.result;
            }
        });
    }
    
    public static void copy(final URL from, final OutputStream to) throws IOException {
        asByteSource(from).copyTo(to);
    }
    
    public static URL getResource(final String resourceName) {
        final ClassLoader loader = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(), Resources.class.getClassLoader());
        final URL url = loader.getResource(resourceName);
        Preconditions.checkArgument(url != null, "resource %s not found.", resourceName);
        return url;
    }
    
    public static URL getResource(final Class<?> contextClass, final String resourceName) {
        final URL url = contextClass.getResource(resourceName);
        Preconditions.checkArgument(url != null, "resource %s relative to %s not found.", resourceName, contextClass.getName());
        return url;
    }
    
    private static final class UrlByteSource extends ByteSource
    {
        private final URL url;
        
        private UrlByteSource(final URL url) {
            this.url = Preconditions.checkNotNull(url);
        }
        
        @Override
        public InputStream openStream() throws IOException {
            return this.url.openStream();
        }
        
        @Override
        public String toString() {
            final String value = String.valueOf(String.valueOf(this.url));
            return new StringBuilder(24 + value.length()).append("Resources.asByteSource(").append(value).append(")").toString();
        }
    }
}
