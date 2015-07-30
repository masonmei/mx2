// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.cache;

import com.newrelic.agent.deps.com.google.common.base.Objects;
import com.newrelic.agent.deps.com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import java.util.Map;

@Beta
@GwtCompatible
public final class RemovalNotification<K, V> implements Map.Entry<K, V>
{
    @Nullable
    private final K key;
    @Nullable
    private final V value;
    private final RemovalCause cause;
    private static final long serialVersionUID = 0L;
    
    RemovalNotification(@Nullable final K key, @Nullable final V value, final RemovalCause cause) {
        this.key = key;
        this.value = value;
        this.cause = Preconditions.checkNotNull(cause);
    }
    
    public RemovalCause getCause() {
        return this.cause;
    }
    
    public boolean wasEvicted() {
        return this.cause.wasEvicted();
    }
    
    @Nullable
    @Override
    public K getKey() {
        return this.key;
    }
    
    @Nullable
    @Override
    public V getValue() {
        return this.value;
    }
    
    @Override
    public final V setValue(final V value) {
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
