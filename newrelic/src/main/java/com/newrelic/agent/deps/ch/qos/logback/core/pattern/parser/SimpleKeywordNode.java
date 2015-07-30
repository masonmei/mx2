// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser;

import java.util.List;

public class SimpleKeywordNode extends FormattingNode
{
    List<String> optionList;
    
    SimpleKeywordNode(final Object value) {
        super(1, value);
    }
    
    protected SimpleKeywordNode(final int type, final Object value) {
        super(type, value);
    }
    
    public List<String> getOptions() {
        return this.optionList;
    }
    
    public void setOptions(final List<String> optionList) {
        this.optionList = optionList;
    }
    
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof SimpleKeywordNode)) {
            return false;
        }
        final SimpleKeywordNode r = (SimpleKeywordNode)o;
        return (this.optionList != null) ? this.optionList.equals(r.optionList) : (r.optionList == null);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        if (this.optionList == null) {
            buf.append("KeyWord(" + this.value + "," + this.formatInfo + ")");
        }
        else {
            buf.append("KeyWord(" + this.value + ", " + this.formatInfo + "," + this.optionList + ")");
        }
        buf.append(this.printNext());
        return buf.toString();
    }
}
