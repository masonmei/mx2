// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson;

import java.lang.reflect.Type;

public interface JsonSerializationContext
{
    JsonElement serialize(Object p0);
    
    JsonElement serialize(Object p0, Type p1);
}
