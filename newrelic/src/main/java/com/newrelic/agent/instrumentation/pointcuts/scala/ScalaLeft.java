// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "scala/util/Left", "scala/Left" })
public interface ScalaLeft extends Either
{
    public static final String CLASS = "scala/util/Left";
    public static final String CLASS2 = "scala/Left";
    
    @FieldAccessor(fieldName = "a", existingField = true)
    Object get();
}
