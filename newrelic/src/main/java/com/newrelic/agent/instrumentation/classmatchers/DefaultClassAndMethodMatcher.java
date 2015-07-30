// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class DefaultClassAndMethodMatcher implements ClassAndMethodMatcher
{
    protected final ClassMatcher classMatcher;
    protected final MethodMatcher methodMatcher;
    
    public DefaultClassAndMethodMatcher(final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        this.classMatcher = classMatcher;
        this.methodMatcher = methodMatcher;
    }
    
    public ClassMatcher getClassMatcher() {
        return this.classMatcher;
    }
    
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }
}
