// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Arrays;
import java.util.Collection;

public abstract class ManyMethodMatcher implements MethodMatcher
{
    protected final Collection<MethodMatcher> methodMatchers;
    
    protected ManyMethodMatcher(final MethodMatcher... methodMatchers) {
        this(Arrays.asList(methodMatchers));
    }
    
    public ManyMethodMatcher(final Collection<MethodMatcher> methodMatchers) {
        this.methodMatchers = methodMatchers;
    }
    
    public Collection<MethodMatcher> getMethodMatchers() {
        return this.methodMatchers;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.methodMatchers == null) ? 0 : this.methodMatchers.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ManyMethodMatcher other = (ManyMethodMatcher)obj;
        if (this.methodMatchers == null) {
            if (other.methodMatchers != null) {
                return false;
            }
        }
        else if (this.methodMatchers.size() != other.methodMatchers.size() || !this.methodMatchers.containsAll(other.methodMatchers)) {
            return false;
        }
        return true;
    }
    
    public Method[] getExactMethods() {
        final List<Method> methods = (List<Method>)Lists.newArrayList();
        for (final MethodMatcher matcher : this.methodMatchers) {
            final Method[] exactMethods = matcher.getExactMethods();
            if (exactMethods == null) {
                return null;
            }
            methods.addAll(Arrays.asList(exactMethods));
        }
        return methods.toArray(new Method[methods.size()]);
    }
}
