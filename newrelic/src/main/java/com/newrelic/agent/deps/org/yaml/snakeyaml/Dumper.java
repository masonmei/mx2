// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml;

import java.io.IOException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.serializer.Serializer;
import com.newrelic.agent.deps.org.yaml.snakeyaml.emitter.Emitter;
import com.newrelic.agent.deps.org.yaml.snakeyaml.resolver.Resolver;
import java.io.Writer;
import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.representer.Representer;

public class Dumper
{
    private final Representer representer;
    private final DumperOptions options;
    private boolean attached;
    
    public Dumper(final Representer representer, final DumperOptions options) {
        this.attached = false;
        this.representer = representer;
        this.options = options;
    }
    
    public Dumper(final DumperOptions options) {
        this(new Representer(options.getDefaultStyle().getChar(), options.getDefaultFlowStyle().getStyleBoolean()), options);
    }
    
    public void dump(final Iterator<?> iter, final Writer output, final Resolver resolver) {
        final Serializer s = new Serializer(new Emitter(output, this.options), resolver, this.options);
        try {
            s.open();
            while (iter.hasNext()) {
                this.representer.represent(s, iter.next());
            }
            s.close();
        }
        catch (IOException e) {
            throw new YAMLException(e);
        }
    }
    
    void setAttached() {
        if (!this.attached) {
            this.attached = true;
            return;
        }
        throw new YAMLException("Dumper cannot be shared.");
    }
}
