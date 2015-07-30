// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson.internal.bind;

import java.util.Iterator;
import com.newrelic.agent.deps.com.google.gson.JsonSyntaxException;
import com.newrelic.agent.deps.com.google.gson.stream.JsonToken;
import com.newrelic.agent.deps.com.google.gson.internal.$Gson$Types;
import java.util.LinkedHashMap;
import com.newrelic.agent.deps.com.google.gson.annotations.JsonAdapter;
import com.newrelic.agent.deps.com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.newrelic.agent.deps.com.google.gson.stream.JsonWriter;
import java.lang.reflect.Type;
import com.newrelic.agent.deps.com.google.gson.internal.Primitives;
import java.util.Map;
import com.newrelic.agent.deps.com.google.gson.internal.ObjectConstructor;
import com.newrelic.agent.deps.com.google.gson.TypeAdapter;
import com.newrelic.agent.deps.com.google.gson.reflect.TypeToken;
import com.newrelic.agent.deps.com.google.gson.Gson;
import com.newrelic.agent.deps.com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import com.newrelic.agent.deps.com.google.gson.internal.Excluder;
import com.newrelic.agent.deps.com.google.gson.FieldNamingStrategy;
import com.newrelic.agent.deps.com.google.gson.internal.ConstructorConstructor;
import com.newrelic.agent.deps.com.google.gson.TypeAdapterFactory;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory
{
    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;
    
    public ReflectiveTypeAdapterFactory(final ConstructorConstructor constructorConstructor, final FieldNamingStrategy fieldNamingPolicy, final Excluder excluder) {
        this.constructorConstructor = constructorConstructor;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.excluder = excluder;
    }
    
    public boolean excludeField(final Field f, final boolean serialize) {
        return excludeField(f, serialize, this.excluder);
    }
    
    static boolean excludeField(final Field f, final boolean serialize, final Excluder excluder) {
        return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
    }
    
    private String getFieldName(final Field f) {
        return getFieldName(this.fieldNamingPolicy, f);
    }
    
    static String getFieldName(final FieldNamingStrategy fieldNamingPolicy, final Field f) {
        final SerializedName serializedName = f.getAnnotation(SerializedName.class);
        return (serializedName == null) ? fieldNamingPolicy.translateName(f) : serializedName.value();
    }
    
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        final Class<? super T> raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null;
        }
        final ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
        return new Adapter<T>((ObjectConstructor)constructor, (Map)this.getBoundFields(gson, type, raw));
    }
    
    private BoundField createBoundField(final Gson context, final Field field, final String name, final TypeToken<?> fieldType, final boolean serialize, final boolean deserialize) {
        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        return new BoundField(name, serialize, deserialize) {
            final TypeAdapter<?> typeAdapter = ReflectiveTypeAdapterFactory.this.getFieldAdapter(context, field, fieldType);
            
            void write(final JsonWriter writer, final Object value) throws IOException, IllegalAccessException {
                final Object fieldValue = field.get(value);
                final TypeAdapter t = new TypeAdapterRuntimeTypeWrapper(context, this.typeAdapter, fieldType.getType());
                t.write(writer, fieldValue);
            }
            
            void read(final JsonReader reader, final Object value) throws IOException, IllegalAccessException {
                final Object fieldValue = this.typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    field.set(value, fieldValue);
                }
            }
            
            public boolean writeField(final Object value) throws IOException, IllegalAccessException {
                if (!this.serialized) {
                    return false;
                }
                final Object fieldValue = field.get(value);
                return fieldValue != value;
            }
        };
    }
    
    private TypeAdapter<?> getFieldAdapter(final Gson gson, final Field field, final TypeToken<?> fieldType) {
        final JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        if (annotation != null) {
            final TypeAdapter<?> adapter = JsonAdapterAnnotationTypeAdapterFactory.getTypeAdapter(this.constructorConstructor, gson, fieldType, annotation);
            if (adapter != null) {
                return adapter;
            }
        }
        return gson.getAdapter(fieldType);
    }
    
    private Map<String, BoundField> getBoundFields(final Gson context, TypeToken<?> type, Class<?> raw) {
        final Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
        if (raw.isInterface()) {
            return result;
        }
        final Type declaredType = type.getType();
        while (raw != Object.class) {
            final Field[] arr$;
            final Field[] fields = arr$ = raw.getDeclaredFields();
            for (final Field field : arr$) {
                final boolean serialize = this.excludeField(field, true);
                final boolean deserialize = this.excludeField(field, false);
                if (serialize || deserialize) {
                    field.setAccessible(true);
                    final Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                    final BoundField boundField = this.createBoundField(context, field, this.getFieldName(field), TypeToken.get(fieldType), serialize, deserialize);
                    final BoundField previous = result.put(boundField.name, boundField);
                    if (previous != null) {
                        throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
                    }
                }
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }
    
    abstract static class BoundField
    {
        final String name;
        final boolean serialized;
        final boolean deserialized;
        
        protected BoundField(final String name, final boolean serialized, final boolean deserialized) {
            this.name = name;
            this.serialized = serialized;
            this.deserialized = deserialized;
        }
        
        abstract boolean writeField(final Object p0) throws IOException, IllegalAccessException;
        
        abstract void write(final JsonWriter p0, final Object p1) throws IOException, IllegalAccessException;
        
        abstract void read(final JsonReader p0, final Object p1) throws IOException, IllegalAccessException;
    }
    
    public static final class Adapter<T> extends TypeAdapter<T>
    {
        private final ObjectConstructor<T> constructor;
        private final Map<String, BoundField> boundFields;
        
        private Adapter(final ObjectConstructor<T> constructor, final Map<String, BoundField> boundFields) {
            this.constructor = constructor;
            this.boundFields = boundFields;
        }
        
        public T read(final JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            final T instance = this.constructor.construct();
            try {
                in.beginObject();
                while (in.hasNext()) {
                    final String name = in.nextName();
                    final BoundField field = this.boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in.skipValue();
                    }
                    else {
                        field.read(in, instance);
                    }
                }
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalAccessException e2) {
                throw new AssertionError((Object)e2);
            }
            in.endObject();
            return instance;
        }
        
        public void write(final JsonWriter out, final T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            try {
                for (final BoundField boundField : this.boundFields.values()) {
                    if (boundField.writeField(value)) {
                        out.name(boundField.name);
                        boundField.write(out, value);
                    }
                }
            }
            catch (IllegalAccessException e) {
                throw new AssertionError();
            }
            out.endObject();
        }
    }
}
