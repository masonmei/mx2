// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.Map;

public final class DocumentStartEvent extends Event
{
    private final boolean explicit;
    private final Integer[] version;
    private final Map<String, String> tags;
    
    public DocumentStartEvent(final Mark startMark, final Mark endMark, final boolean explicit, final Integer[] version, final Map<String, String> tags) {
        super(startMark, endMark);
        this.explicit = explicit;
        this.version = version;
        this.tags = tags;
    }
    
    public boolean getExplicit() {
        return this.explicit;
    }
    
    public Integer[] getVersion() {
        return this.version;
    }
    
    public Map<String, String> getTags() {
        return this.tags;
    }
}
