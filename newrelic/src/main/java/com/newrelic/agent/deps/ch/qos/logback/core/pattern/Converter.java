// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

public abstract class Converter<E>
{
    Converter<E> next;
    
    public abstract String convert(final E p0);
    
    public void write(final StringBuilder buf, final E event) {
        buf.append(this.convert(event));
    }
    
    public final void setNext(final Converter<E> next) {
        if (this.next != null) {
            throw new IllegalStateException("Next converter has been already set");
        }
        this.next = next;
    }
    
    public final Converter<E> getNext() {
        return this.next;
    }
}
