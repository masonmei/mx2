// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson;

import com.newrelic.agent.deps.com.google.gson.reflect.TypeToken;

public interface TypeAdapterFactory
{
     <T> TypeAdapter<T> create(Gson p0, TypeToken<T> p1);
}
