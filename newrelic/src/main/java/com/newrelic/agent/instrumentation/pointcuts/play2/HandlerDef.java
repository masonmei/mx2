// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play2;

import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "play/core/Router$HandlerDef" })
public interface HandlerDef
{
    public static final String CLASS_NAME = "play/core/Router$HandlerDef";
    
    String controller();
    
    String method();
}
