// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.util.proxy;

public interface ProxyObject extends Proxy
{
    void setHandler(MethodHandler p0);
    
    MethodHandler getHandler();
}
