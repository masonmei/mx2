// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;

public class AccessMethodMatcher implements MethodMatcher
{
    private final int accessFlags;
    
    public AccessMethodMatcher(final int accessFlags) {
        this.accessFlags = accessFlags;
    }
    
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        return access == -1 || (access & this.accessFlags) == this.accessFlags;
    }
    
    public Method[] getExactMethods() {
        return null;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.accessFlags;
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
        final AccessMethodMatcher other = (AccessMethodMatcher)obj;
        return this.accessFlags == other.accessFlags;
    }
}
