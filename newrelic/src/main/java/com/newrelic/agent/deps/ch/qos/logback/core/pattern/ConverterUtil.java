// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;

public class ConverterUtil
{
    public static void startConverters(final Converter head) {
        for (Converter c = head; c != null; c = c.getNext()) {
            if (c instanceof CompositeConverter) {
                final CompositeConverter cc = (CompositeConverter)c;
                final Converter childConverter = cc.childConverter;
                startConverters(childConverter);
                cc.start();
            }
            else if (c instanceof DynamicConverter) {
                final DynamicConverter dc = (DynamicConverter)c;
                dc.start();
            }
        }
    }
    
    public static <E> Converter<E> findTail(final Converter<E> head) {
        Converter<E> p;
        Converter<E> next;
        for (p = head; p != null; p = next) {
            next = p.getNext();
            if (next == null) {
                break;
            }
        }
        return p;
    }
    
    public static <E> void setContextForConverters(final Context context, final Converter<E> head) {
        for (Converter c = head; c != null; c = c.getNext()) {
            if (c instanceof ContextAware) {
                ((ContextAware)c).setContext(context);
            }
        }
    }
}
