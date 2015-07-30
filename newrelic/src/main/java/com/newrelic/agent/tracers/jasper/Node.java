// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

public interface Node
{
    Node getParent() throws Exception;
    
    String getQName() throws Exception;
}
