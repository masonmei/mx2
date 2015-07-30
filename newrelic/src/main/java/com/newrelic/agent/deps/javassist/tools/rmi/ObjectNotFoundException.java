// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.tools.rmi;

public class ObjectNotFoundException extends Exception
{
    public ObjectNotFoundException(final String name) {
        super(name + " is not exported");
    }
    
    public ObjectNotFoundException(final String name, final Exception e) {
        super(name + " because of " + e.toString());
    }
}
