// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.color;

public class BoldWhiteCompositeConverter<E> extends ForegroundCompositeConverterBase<E>
{
    protected String getForegroundColorCode(final E event) {
        return "1;37";
    }
}
