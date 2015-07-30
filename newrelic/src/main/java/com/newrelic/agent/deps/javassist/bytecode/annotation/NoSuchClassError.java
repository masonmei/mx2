// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.bytecode.annotation;

public class NoSuchClassError extends Error
{
    private String className;
    
    public NoSuchClassError(final String className, final Error cause) {
        super(cause.toString(), cause);
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
}
