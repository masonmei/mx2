// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.nodes;

import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.List;

public class MappingNode extends CollectionNode
{
    private Class<?> keyType;
    private Class<?> valueType;
    
    public MappingNode(final String tag, final List<Node[]> value, final Mark startMark, final Mark endMark, final Boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
        this.keyType = Object.class;
        this.valueType = Object.class;
    }
    
    public MappingNode(final String tag, final List<Node[]> value, final Boolean flowStyle) {
        super(tag, value, null, null, flowStyle);
    }
    
    public NodeId getNodeId() {
        return NodeId.mapping;
    }
    
    public List<Node[]> getValue() {
        final List<Node[]> mapping = (List<Node[]>)super.getValue();
        for (final Node[] nodes : mapping) {
            nodes[0].setType(this.keyType);
            nodes[1].setType(this.valueType);
        }
        return mapping;
    }
    
    public void setValue(final List<Node[]> merge) {
        this.value = merge;
    }
    
    public void setKeyType(final Class<?> keyType) {
        this.keyType = keyType;
    }
    
    public void setValueType(final Class<?> valueType) {
        this.valueType = valueType;
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for (final Node[] node : this.getValue()) {
            buf.append("{ key=");
            buf.append(node[0]);
            buf.append("; value=");
            buf.append(System.identityHashCode(node[1]));
            buf.append(" }");
        }
        final String values = buf.toString();
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", values=" + values + ")>";
    }
}
