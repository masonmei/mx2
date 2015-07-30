// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.akka;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "akka/dispatch/AbstractPromise" })
public interface AkkaTracerHolder
{
    @FieldAccessor(fieldName = "tracer", volatileAccess = true)
    Object _nr_getTracer();
    
    @FieldAccessor(fieldName = "tracer", volatileAccess = true)
    void _nr_setTracer(Object p0);
}
