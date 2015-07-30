// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml;

import com.newrelic.agent.deps.org.yaml.snakeyaml.resolver.Resolver;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.BaseConstructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Constructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.reader.UnicodeReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class JavaBeanParser
{
    public static <T> T load(final String yaml, final Class<T> javabean) {
        final Loader loader = createLoader(javabean);
        return (T)loader.load(new StringReader(yaml));
    }
    
    public static <T> T load(final InputStream io, final Class<T> javabean) {
        final Loader loader = createLoader(javabean);
        return (T)loader.load(new UnicodeReader(io));
    }
    
    public static <T> T load(final Reader io, final Class<T> javabean) {
        final Loader loader = createLoader(javabean);
        return (T)loader.load(io);
    }
    
    private static Loader createLoader(final Class<?> clazz) {
        final Loader loader = new Loader(new Constructor(clazz));
        final Resolver resolver = new Resolver();
        loader.setResolver(resolver);
        return loader;
    }
}
