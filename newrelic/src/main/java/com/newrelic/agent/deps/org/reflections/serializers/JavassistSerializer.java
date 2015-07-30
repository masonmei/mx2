// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.serializers;

import java.io.File;
import com.newrelic.agent.deps.org.reflections.Reflections;
import java.io.InputStream;
import com.newrelic.agent.deps.org.reflections.Configuration;
import com.newrelic.agent.deps.org.reflections.adapters.MetadataAdapter;

public class JavassistSerializer implements Serializer
{
    private final MetadataAdapter javassist;
    
    public JavassistSerializer(final Configuration configuration) {
        this.javassist = configuration.getMetadataAdapter();
    }
    
    public Reflections read(final InputStream inputStream) {
        return null;
    }
    
    public File save(final Reflections reflections, final String filename) {
        return null;
    }
    
    public String toString(final Reflections reflections) {
        return null;
    }
}
