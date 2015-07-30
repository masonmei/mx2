// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.assembly;

import com.newrelic.agent.deps.org.reflections.Configuration;
import com.newrelic.agent.deps.org.reflections.Reflections;
import com.newrelic.agent.deps.org.reflections.serializers.Serializer;
import com.newrelic.agent.deps.org.reflections.serializers.JsonSerializer;
import com.newrelic.agent.deps.org.reflections.util.ConfigurationBuilder;

public class BuildManifest
{
    public static void main(final String[] args) {
        final String buildDir = args[0];
        final Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("com.newrelic").setSerializer(new JsonSerializer()));
        reflections.save(buildDir + "/newrelic-manifest.json");
    }
}
