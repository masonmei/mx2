// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;

public class MethodWithAccess
{
    protected final boolean isStatic;
    protected final Method method;
    
    public MethodWithAccess(final boolean isStatic, final Method method) {
        this.method = method;
        this.isStatic = isStatic;
    }
    
    public boolean isStatic() {
        return this.isStatic;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MethodWithAccess that = (MethodWithAccess)o;
        if (this.isStatic != that.isStatic) {
            return false;
        }
        if (this.method != null) {
            if (this.method.equals(that.method)) {
                return true;
            }
        }
        else if (that.method == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = this.isStatic ? 1 : 0;
        result = 31 * result + ((this.method != null) ? this.method.hashCode() : 0);
        return result;
    }
    
    public String toString() {
        return this.isStatic ? ("static " + this.method) : this.method.toString();
    }
}
