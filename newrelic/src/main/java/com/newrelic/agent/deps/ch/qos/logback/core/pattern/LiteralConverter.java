// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

public final class LiteralConverter<E> extends Converter<E>
{
    String literal;
    
    public LiteralConverter(final String literal) {
        this.literal = literal;
    }
    
    public String convert(final E o) {
        return this.literal;
    }
}
