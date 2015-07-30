// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

public abstract class CompositeConverter<E> extends DynamicConverter<E>
{
    Converter<E> childConverter;
    
    public String convert(final E event) {
        final StringBuilder buf = new StringBuilder();
        for (Converter<E> c = this.childConverter; c != null; c = c.next) {
            c.write(buf, event);
        }
        final String intermediary = buf.toString();
        return this.transform(event, intermediary);
    }
    
    protected abstract String transform(final E p0, final String p1);
    
    public Converter<E> getChildConverter() {
        return this.childConverter;
    }
    
    public void setChildConverter(final Converter<E> child) {
        this.childConverter = child;
    }
    
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("CompositeConverter<");
        if (this.formattingInfo != null) {
            buf.append(this.formattingInfo);
        }
        if (this.childConverter != null) {
            buf.append(", children: ").append(this.childConverter);
        }
        buf.append(">");
        return buf.toString();
    }
}
