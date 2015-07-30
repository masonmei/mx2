// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

public class IdentityCompositeConverter<E> extends CompositeConverter<E>
{
    protected String transform(final E event, final String in) {
        return in;
    }
}
