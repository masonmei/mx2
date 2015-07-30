// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import com.newrelic.agent.deps.ch.qos.logback.core.pattern.DynamicConverter;

public class IntegerTokenConverter extends DynamicConverter implements MonoTypedConverter
{
    public static final String CONVERTER_KEY = "i";
    
    public String convert(final int i) {
        return Integer.toString(i);
    }
    
    public String convert(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Integer) {
            final Integer i = (Integer)o;
            return this.convert((int)i);
        }
        throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
    }
    
    public boolean isApplicable(final Object o) {
        return o instanceof Integer;
    }
}
