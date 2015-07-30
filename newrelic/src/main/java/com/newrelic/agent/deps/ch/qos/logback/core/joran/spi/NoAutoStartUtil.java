// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

public class NoAutoStartUtil
{
    public static boolean notMarkedWithNoAutoStart(final Object o) {
        if (o == null) {
            return false;
        }
        final Class<?> clazz = o.getClass();
        final NoAutoStart a = clazz.getAnnotation(NoAutoStart.class);
        return a == null;
    }
}
