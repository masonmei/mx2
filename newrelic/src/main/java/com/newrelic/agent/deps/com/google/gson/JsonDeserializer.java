// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson;

import java.lang.reflect.Type;

public interface JsonDeserializer<T>
{
    T deserialize(JsonElement p0, Type p1, JsonDeserializationContext p2) throws JsonParseException;
}
