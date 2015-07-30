// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.runtime;

public class DotClass
{
    public static NoClassDefFoundError fail(final ClassNotFoundException e) {
        return new NoClassDefFoundError(e.getMessage());
    }
}
