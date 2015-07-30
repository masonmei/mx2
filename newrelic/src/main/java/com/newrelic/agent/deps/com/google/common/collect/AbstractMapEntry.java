// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.base.Objects;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V>
{
    @Override
    public abstract K getKey();
    
    @Override
    public abstract V getValue();
    
    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(@Nullable final Object object) {
        if (object instanceof Map.Entry) {
            final Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
            return Objects.equal(this.getKey(), that.getKey()) && Objects.equal(this.getValue(), that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final K k = this.getKey();
        final V v = this.getValue();
        return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
    }
    
    @Override
    public String toString() {
        final String value = String.valueOf(String.valueOf(this.getKey()));
        final String value2 = String.valueOf(String.valueOf(this.getValue()));
        return new StringBuilder(1 + value.length() + value2.length()).append(value).append("=").append(value2).toString();
    }
}
