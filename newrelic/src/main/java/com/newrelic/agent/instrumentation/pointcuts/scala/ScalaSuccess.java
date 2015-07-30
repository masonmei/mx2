// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "scala/util/Success" })
public interface ScalaSuccess extends ScalaTry
{
    public static final String CLASS = "scala/util/Success";
    
    @FieldAccessor(fieldName = "value", existingField = true)
    Object _nr_value();
}
