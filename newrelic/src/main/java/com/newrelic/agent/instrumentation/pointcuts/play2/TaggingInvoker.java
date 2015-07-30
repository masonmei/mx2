// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play2;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "play/core/Router$Routes$TaggingInvoker" })
public interface TaggingInvoker
{
    @FieldAccessor(fieldName = "handlerDef")
    Object getHandlerDef();
    
    @FieldAccessor(fieldName = "handlerDef")
    void setHandlerDef(Object p0);
}
