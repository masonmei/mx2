// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public abstract class NodeEvent extends Event
{
    private final String anchor;
    
    public NodeEvent(final String anchor, final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        this.anchor = anchor;
    }
    
    public String getAnchor() {
        return this.anchor;
    }
    
    protected String getArguments() {
        return "anchor=" + this.anchor;
    }
}
