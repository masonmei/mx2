// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson.internal.bind;

import com.newrelic.agent.deps.com.google.gson.annotations.JsonAdapter;
import com.newrelic.agent.deps.com.google.gson.TypeAdapter;
import com.newrelic.agent.deps.com.google.gson.reflect.TypeToken;
import com.newrelic.agent.deps.com.google.gson.Gson;
import com.newrelic.agent.deps.com.google.gson.internal.ConstructorConstructor;
import com.newrelic.agent.deps.com.google.gson.TypeAdapterFactory;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory
{
    private final ConstructorConstructor constructorConstructor;
    
    public JsonAdapterAnnotationTypeAdapterFactory(final ConstructorConstructor constructorConstructor) {
        this.constructorConstructor = constructorConstructor;
    }
    
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> targetType) {
        final JsonAdapter annotation = targetType.getRawType().getAnnotation(JsonAdapter.class);
        if (annotation == null) {
            return null;
        }
        return (TypeAdapter<T>)getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
    }
    
    static TypeAdapter<?> getTypeAdapter(final ConstructorConstructor constructorConstructor, final Gson gson, final TypeToken<?> fieldType, final JsonAdapter annotation) {
        final Class<?> value = annotation.value();
        if (TypeAdapter.class.isAssignableFrom(value)) {
            final Class<TypeAdapter<?>> typeAdapter = (Class<TypeAdapter<?>>)value;
            return constructorConstructor.get((TypeToken<TypeAdapter<?>>)TypeToken.get((Class<T>)typeAdapter)).construct();
        }
        if (TypeAdapterFactory.class.isAssignableFrom(value)) {
            final Class<TypeAdapterFactory> typeAdapterFactory = (Class<TypeAdapterFactory>)value;
            return constructorConstructor.get((TypeToken<TypeAdapterFactory>)TypeToken.get((Class<T>)typeAdapterFactory)).construct().create(gson, fieldType);
        }
        throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
    }
}
