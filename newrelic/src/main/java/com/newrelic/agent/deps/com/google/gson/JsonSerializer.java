// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson;

import java.lang.reflect.Type;

public interface JsonSerializer<T>
{
    JsonElement serialize(T p0, Type p1, JsonSerializationContext p2);
}
