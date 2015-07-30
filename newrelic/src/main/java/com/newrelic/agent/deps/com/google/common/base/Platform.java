// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.base;

import java.lang.ref.WeakReference;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform
{
    static long systemNanoTime() {
        return System.nanoTime();
    }
    
    static CharMatcher precomputeCharMatcher(final CharMatcher matcher) {
        return matcher.precomputedInternal();
    }
    
    static <T extends Enum<T>> Optional<T> getEnumIfPresent(final Class<T> enumClass, final String value) {
        final WeakReference<? extends Enum<?>> ref = Enums.getEnumConstants(enumClass).get(value);
        return (ref == null) ? Optional.absent() : Optional.of(enumClass.cast(ref.get()));
    }
}
