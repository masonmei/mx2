// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class ParameterAttributeName
{
    private final String attributeName;
    private final int index;
    private final MethodMatcher methodMatcher;
    
    public ParameterAttributeName(final int index, final String attributeName, final MethodMatcher methodMatcher) {
        this.index = index;
        this.attributeName = attributeName;
        this.methodMatcher = methodMatcher;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }
    
    public String toString() {
        return "ParameterAttributeName [attributeName=" + this.attributeName + ", index=" + this.index + ", methodMatcher=" + this.methodMatcher + "]";
    }
}
