// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml;

import java.util.regex.Pattern;
import com.newrelic.agent.deps.org.yaml.snakeyaml.reader.UnicodeReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.yaml.snakeyaml.resolver.Resolver;

public class Yaml
{
    private final Dumper dumper;
    private final Loader loader;
    private final Resolver resolver;
    private String name;
    
    public Yaml(final DumperOptions options) {
        this(new Loader(), new Dumper(options));
    }
    
    public Yaml(final Dumper dumper) {
        this(new Loader(), dumper);
    }
    
    public Yaml(final Loader loader) {
        this(loader, new Dumper(new DumperOptions()));
    }
    
    public Yaml(final Loader loader, final Dumper dumper) {
        (this.loader = loader).setAttached();
        (this.dumper = dumper).setAttached();
        this.resolver = new Resolver();
        this.loader.setResolver(this.resolver);
        this.name = "Yaml:" + System.identityHashCode(this);
    }
    
    public Yaml() {
        this(new Loader(), new Dumper(new DumperOptions()));
    }
    
    public String dump(final Object data) {
        final List<Object> lst = new ArrayList<Object>(1);
        lst.add(data);
        return this.dumpAll(lst.iterator());
    }
    
    public String dumpAll(final Iterator<?> data) {
        final StringWriter buffer = new StringWriter();
        this.dumpAll(data, buffer);
        return buffer.toString();
    }
    
    public void dump(final Object data, final Writer output) {
        final List<Object> lst = new ArrayList<Object>(1);
        lst.add(data);
        this.dumpAll(lst.iterator(), output);
    }
    
    public void dumpAll(final Iterator<?> data, final Writer output) {
        this.dumper.dump(data, output, this.resolver);
    }
    
    public Object load(final String yaml) {
        return this.loader.load(new StringReader(yaml));
    }
    
    public Object load(final InputStream io) {
        return this.loader.load(new UnicodeReader(io));
    }
    
    public Object load(final Reader io) {
        return this.loader.load(io);
    }
    
    public Iterable<Object> loadAll(final Reader yaml) {
        return this.loader.loadAll(yaml);
    }
    
    public Iterable<Object> loadAll(final String yaml) {
        return this.loadAll(new StringReader(yaml));
    }
    
    public Iterable<Object> loadAll(final InputStream yaml) {
        return this.loadAll(new UnicodeReader(yaml));
    }
    
    public void addImplicitResolver(final String tag, final Pattern regexp, final String first) {
        this.resolver.addImplicitResolver(tag, regexp, first);
    }
    
    public String toString() {
        return this.name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
