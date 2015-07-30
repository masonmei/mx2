// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;

public final class AllMethodsMatcher implements MethodMatcher
{
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        return !"<init>".equals(name);
    }
    
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && this.getClass() == obj.getClass());
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public Method[] getExactMethods() {
        return null;
    }
}
