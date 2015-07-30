// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.tools.rmi;

public class RemoteException extends RuntimeException
{
    public RemoteException(final String msg) {
        super(msg);
    }
    
    public RemoteException(final Exception e) {
        super("by " + e.toString());
    }
}
