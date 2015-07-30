// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser;

public class CompositeNode extends SimpleKeywordNode
{
    Node childNode;
    
    CompositeNode(final String keyword) {
        super(2, keyword);
    }
    
    public Node getChildNode() {
        return this.childNode;
    }
    
    public void setChildNode(final Node childNode) {
        this.childNode = childNode;
    }
    
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof CompositeNode)) {
            return false;
        }
        final CompositeNode r = (CompositeNode)o;
        return (this.childNode != null) ? this.childNode.equals(r.childNode) : (r.childNode == null);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        if (this.childNode != null) {
            buf.append("CompositeNode(" + this.childNode + ")");
        }
        else {
            buf.append("CompositeNode(no child)");
        }
        buf.append(this.printNext());
        return buf.toString();
    }
}
