// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist;

public class NotFoundException extends Exception
{
    public NotFoundException(final String msg) {
        super(msg);
    }
    
    public NotFoundException(final String msg, final Exception e) {
        super(msg + " because of " + e.toString());
    }
}
