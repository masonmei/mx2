// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import java.util.Iterator;
import java.util.Set;

public final class AndMethodMatcher extends ManyMethodMatcher
{
    protected AndMethodMatcher(final MethodMatcher... methodMatchers) {
        super(methodMatchers);
    }
    
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        for (final MethodMatcher matcher : this.methodMatchers) {
            if (!matcher.matches(access, name, desc, annotations)) {
                return false;
            }
        }
        return true;
    }
    
    public static final MethodMatcher getMethodMatcher(final MethodMatcher... matchers) {
        if (matchers.length == 1) {
            return matchers[0];
        }
        return new AndMethodMatcher(matchers);
    }
    
    public String toString() {
        return "And Match " + this.methodMatchers;
    }
}
