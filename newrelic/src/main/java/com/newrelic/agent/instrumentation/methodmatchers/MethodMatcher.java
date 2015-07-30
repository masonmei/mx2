// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;

public interface MethodMatcher
{
    public static final Set<String> UNSPECIFIED_ANNOTATIONS = ImmutableSet.of();
    public static final int UNSPECIFIED_ACCESS = -1;
    
    boolean matches(int p0, String p1, String p2, Set<String> p3);
    
    boolean equals(Object p0);
    
    Method[] getExactMethods();
}
