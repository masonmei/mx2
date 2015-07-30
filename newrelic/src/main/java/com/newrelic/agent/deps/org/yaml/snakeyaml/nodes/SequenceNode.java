// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.nodes;

import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.List;

public class SequenceNode extends CollectionNode
{
    private Class<?> listType;
    
    public SequenceNode(final String tag, final List<Node> value, final Mark startMark, final Mark endMark, final Boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
        this.listType = Object.class;
    }
    
    public SequenceNode(final String tag, final List<Node> value, final Boolean flowStyle) {
        this(tag, value, null, null, flowStyle);
    }
    
    public NodeId getNodeId() {
        return NodeId.sequence;
    }
    
    public List<Node> getValue() {
        final List<Node> value = (List<Node>)super.getValue();
        for (final Node node : value) {
            node.setType(this.listType);
        }
        return value;
    }
    
    public void setListType(final Class<?> listType) {
        this.listType = listType;
    }
}
