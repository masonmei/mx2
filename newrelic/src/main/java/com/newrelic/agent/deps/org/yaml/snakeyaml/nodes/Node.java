// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.nodes;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public abstract class Node
{
    private String tag;
    protected Object value;
    private Mark startMark;
    protected Mark endMark;
    private Class<?> type;
    
    public Node(final String tag, final Object value, final Mark startMark, final Mark endMark) {
        this.setTag(tag);
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        this.startMark = startMark;
        this.endMark = endMark;
        this.type = Object.class;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
    }
    
    public abstract NodeId getNodeId();
    
    public Mark getStartMark() {
        return this.startMark;
    }
    
    public void setTag(final String tag) {
        if (tag == null) {
            throw new NullPointerException("tag in a Node is required.");
        }
        this.tag = tag;
    }
    
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public void setType(final Class<?> type) {
        this.type = type;
    }
}
