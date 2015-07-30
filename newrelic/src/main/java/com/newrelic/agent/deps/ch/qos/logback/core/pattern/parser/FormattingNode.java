// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser;

import com.newrelic.agent.deps.ch.qos.logback.core.pattern.FormatInfo;

public class FormattingNode extends Node
{
    FormatInfo formatInfo;
    
    FormattingNode(final int type) {
        super(type);
    }
    
    FormattingNode(final int type, final Object value) {
        super(type, value);
    }
    
    public FormatInfo getFormatInfo() {
        return this.formatInfo;
    }
    
    public void setFormatInfo(final FormatInfo formatInfo) {
        this.formatInfo = formatInfo;
    }
    
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof FormattingNode)) {
            return false;
        }
        final FormattingNode r = (FormattingNode)o;
        return (this.formatInfo != null) ? this.formatInfo.equals(r.formatInfo) : (r.formatInfo == null);
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.formatInfo != null) ? this.formatInfo.hashCode() : 0);
        return result;
    }
}
