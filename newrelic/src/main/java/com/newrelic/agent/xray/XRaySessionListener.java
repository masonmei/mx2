// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.xray;

public interface XRaySessionListener
{
    void xraySessionCreated(XRaySession p0);
    
    void xraySessionRemoved(XRaySession p0);
}
