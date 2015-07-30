// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "scala/util/Failure" })
public interface ScalaFailure extends ScalaTry
{
    public static final String CLASS = "scala/util/Failure";
    
    @FieldAccessor(fieldName = "exception", existingField = true)
    Throwable _nr_exception();
}
