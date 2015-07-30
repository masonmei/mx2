// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.nodes;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public class ScalarNode extends Node
{
    private Character style;
    
    public ScalarNode(final String tag, final String value, final Mark startMark, final Mark endMark, final Character style) {
        super(tag, value, startMark, endMark);
        this.style = style;
    }
    
    public Character getStyle() {
        return this.style;
    }
    
    public NodeId getNodeId() {
        return NodeId.scalar;
    }
}
