// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "scala/concurrent/impl/AbstractPromise" })
public interface ScalaTracerHolder
{
    @FieldAccessor(fieldName = "tracer", volatileAccess = true)
    Object _nr_getTracer();
    
    @FieldAccessor(fieldName = "tracer", volatileAccess = true)
    void _nr_setTracer(Object p0);
}
