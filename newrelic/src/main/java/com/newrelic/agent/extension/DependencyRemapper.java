// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import java.util.Iterator;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Remapper;

public class DependencyRemapper extends Remapper
{
    private final Set<String> prefixes;
    private final Map<String, String> oldToNew;
    static final String DEPENDENCY_PREFIX = "com/newrelic/agent/deps/";
    
    public DependencyRemapper(final Set<String> prefixes) {
        this.oldToNew = (Map<String, String>)Maps.newHashMap();
        this.prefixes = fix(prefixes);
    }
    
    private static Set<String> fix(final Set<String> prefixes) {
        final Set<String> fixed = (Set<String>)Sets.newHashSet();
        for (final String prefix : prefixes) {
            if (prefix.startsWith("com/newrelic/agent/deps/")) {
                fixed.add(prefix.substring("com/newrelic/agent/deps/".length()));
            }
            else {
                fixed.add(prefix);
            }
        }
        return (Set<String>)ImmutableSet.copyOf((Collection<?>)fixed);
    }
    
    public String map(final String typeName) {
        for (final String prefix : this.prefixes) {
            if (typeName.startsWith(prefix)) {
                final String newType = "com/newrelic/agent/deps/" + typeName;
                this.oldToNew.put(typeName, newType);
                return newType;
            }
        }
        return super.map(typeName);
    }
    
    public Map<String, String> getRemappings() {
        return this.oldToNew;
    }
    
    Set<String> getPrefixes() {
        return this.prefixes;
    }
}
