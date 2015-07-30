// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public interface ClassAndMethodMatcher
{
    ClassMatcher getClassMatcher();
    
    MethodMatcher getMethodMatcher();
}
