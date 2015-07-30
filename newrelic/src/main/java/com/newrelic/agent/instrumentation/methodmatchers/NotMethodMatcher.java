// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;

public final class NotMethodMatcher implements MethodMatcher
{
    private MethodMatcher methodMatcher;
    
    public NotMethodMatcher(final MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }
    
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        return !this.methodMatcher.matches(access, name, desc, annotations);
    }
    
    public Method[] getExactMethods() {
        return null;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.methodMatcher == null) ? 0 : this.methodMatcher.hashCode());
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
        final NotMethodMatcher other = (NotMethodMatcher)obj;
        if (this.methodMatcher == null) {
            if (other.methodMatcher != null) {
                return false;
            }
        }
        else if (!this.methodMatcher.equals(other.methodMatcher)) {
            return false;
        }
        return true;
    }
}
