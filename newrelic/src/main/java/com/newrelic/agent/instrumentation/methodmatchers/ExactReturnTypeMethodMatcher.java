// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

public class ExactReturnTypeMethodMatcher implements MethodMatcher
{
    private final Type returnType;
    
    public ExactReturnTypeMethodMatcher(final Type returnType) {
        this.returnType = returnType;
    }
    
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        return Type.getReturnType(desc).equals(this.returnType);
    }
    
    public Method[] getExactMethods() {
        return null;
    }
}
