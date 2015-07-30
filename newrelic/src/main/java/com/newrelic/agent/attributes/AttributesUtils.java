// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;

public class AttributesUtils
{
    public static final Map<String, String> appendAttributePrefixes(final Map<String, Map<String, String>> input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String> toReturn = (Map<String, String>)Maps.newHashMap();
        for (final Map.Entry<String, Map<String, String>> current : input.entrySet()) {
            final String prefix = current.getKey();
            final Map<String, String> attributes = current.getValue();
            if (attributes != null) {
                for (final Map.Entry<String, String> att : attributes.entrySet()) {
                    toReturn.put(prefix + att.getKey(), att.getValue());
                }
            }
        }
        return toReturn;
    }
}
