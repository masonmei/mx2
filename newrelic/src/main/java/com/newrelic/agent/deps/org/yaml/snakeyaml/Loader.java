// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.parser.Parser;
import com.newrelic.agent.deps.org.yaml.snakeyaml.composer.Composer;
import com.newrelic.agent.deps.org.yaml.snakeyaml.parser.ParserImpl;
import java.io.Reader;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Constructor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.resolver.Resolver;
import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.BaseConstructor;

public class Loader
{
    protected final BaseConstructor constructor;
    protected Resolver resolver;
    private boolean attached;
    
    public Loader(final BaseConstructor constructor) {
        this.attached = false;
        this.constructor = constructor;
        this.resolver = new Resolver();
    }
    
    public Loader() {
        this(new Constructor());
    }
    
    public Object load(final Reader io) {
        final Composer composer = new Composer(new ParserImpl(new com.newrelic.agent.deps.org.yaml.snakeyaml.reader.Reader(io)), this.resolver);
        this.constructor.setComposer(composer);
        return this.constructor.getSingleData();
    }
    
    public Iterable<Object> loadAll(final Reader yaml) {
        final Composer composer = new Composer(new ParserImpl(new com.newrelic.agent.deps.org.yaml.snakeyaml.reader.Reader(yaml)), this.resolver);
        this.constructor.setComposer(composer);
        final Iterator<Object> result = new Iterator<Object>() {
            public boolean hasNext() {
                return Loader.this.constructor.checkData();
            }
            
            public Object next() {
                return Loader.this.constructor.getData();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new YamlIterable(result);
    }
    
    public void setResolver(final Resolver resolver) {
        this.resolver = resolver;
    }
    
    void setAttached() {
        if (!this.attached) {
            this.attached = true;
            return;
        }
        throw new YAMLException("Loader cannot be shared.");
    }
    
    private class YamlIterable implements Iterable<Object>
    {
        private Iterator<Object> iterator;
        
        public YamlIterable(final Iterator<Object> iterator) {
            this.iterator = iterator;
        }
        
        public Iterator<Object> iterator() {
            return this.iterator;
        }
    }
}
