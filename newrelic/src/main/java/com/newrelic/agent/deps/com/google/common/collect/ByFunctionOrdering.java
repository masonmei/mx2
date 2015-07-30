// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.base.Objects;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.base.Preconditions;
import com.newrelic.agent.deps.com.google.common.base.Function;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class ByFunctionOrdering<F, T> extends Ordering<F> implements Serializable
{
    final Function<F, ? extends T> function;
    final Ordering<T> ordering;
    private static final long serialVersionUID = 0L;
    
    ByFunctionOrdering(final Function<F, ? extends T> function, final Ordering<T> ordering) {
        this.function = Preconditions.checkNotNull(function);
        this.ordering = Preconditions.checkNotNull(ordering);
    }
    
    @Override
    public int compare(final F left, final F right) {
        return this.ordering.compare((T)this.function.apply(left), (T)this.function.apply(right));
    }
    
    @Override
    public boolean equals(@Nullable final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ByFunctionOrdering) {
            final ByFunctionOrdering<?, ?> that = (ByFunctionOrdering<?, ?>)object;
            return this.function.equals(that.function) && this.ordering.equals(that.ordering);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.function, this.ordering);
    }
    
    @Override
    public String toString() {
        final String value = String.valueOf(String.valueOf(this.ordering));
        final String value2 = String.valueOf(String.valueOf(this.function));
        return new StringBuilder(13 + value.length() + value2.length()).append(value).append(".onResultOf(").append(value2).append(")").toString();
    }
}
