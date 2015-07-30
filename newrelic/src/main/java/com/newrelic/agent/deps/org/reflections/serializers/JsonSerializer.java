// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.serializers;

import com.newrelic.agent.deps.com.google.gson.JsonParseException;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.collect.SetMultimap;
import com.newrelic.agent.deps.com.google.gson.JsonArray;
import com.newrelic.agent.deps.com.google.gson.JsonObject;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Multimaps;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.base.Supplier;
import java.util.Collection;
import java.util.HashMap;
import com.newrelic.agent.deps.com.google.gson.JsonDeserializationContext;
import com.newrelic.agent.deps.com.google.gson.JsonDeserializer;
import com.newrelic.agent.deps.com.google.gson.JsonElement;
import com.newrelic.agent.deps.com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import com.newrelic.agent.deps.com.google.gson.JsonSerializer;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import com.newrelic.agent.deps.com.google.gson.GsonBuilder;
import java.io.IOException;
import com.newrelic.agent.deps.com.google.common.io.Files;
import java.nio.charset.Charset;
import com.newrelic.agent.deps.org.reflections.util.Utils;
import java.io.File;
import java.io.Reader;
import java.io.InputStreamReader;
import com.newrelic.agent.deps.org.reflections.Reflections;
import java.io.InputStream;
import com.newrelic.agent.deps.com.google.gson.Gson;

public class JsonSerializer implements Serializer
{
    private Gson gson;
    
    public Reflections read(final InputStream inputStream) {
        return this.getGson().fromJson(new InputStreamReader(inputStream), Reflections.class);
    }
    
    public File save(final Reflections reflections, final String filename) {
        try {
            final File file = Utils.prepareFile(filename);
            Files.write(this.toString(reflections), file, Charset.defaultCharset());
            return file;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String toString(final Reflections reflections) {
        return this.getGson().toJson(reflections);
    }
    
    private Gson getGson() {
        if (this.gson == null) {
            this.gson = new GsonBuilder().registerTypeAdapter(Multimap.class, new com.newrelic.agent.deps.com.google.gson.JsonSerializer<Multimap>() {
                public JsonElement serialize(final Multimap multimap, final Type type, final JsonSerializationContext jsonSerializationContext) {
                    return jsonSerializationContext.serialize(multimap.asMap());
                }
            }).registerTypeAdapter(Multimap.class, new JsonDeserializer<Multimap>() {
                public Multimap deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    final SetMultimap<String, String> map = Multimaps.newSetMultimap(new HashMap<String, Collection<String>>(), new Supplier<Set<String>>() {
                        public Set<String> get() {
                            return (Set<String>)Sets.newHashSet();
                        }
                    });
                    for (final Map.Entry<String, JsonElement> entry : ((JsonObject)jsonElement).entrySet()) {
                        for (final JsonElement element : entry.getValue()) {
                            map.get(entry.getKey()).add(element.getAsString());
                        }
                    }
                    return map;
                }
            }).setPrettyPrinting().create();
        }
        return this.gson;
    }
}
