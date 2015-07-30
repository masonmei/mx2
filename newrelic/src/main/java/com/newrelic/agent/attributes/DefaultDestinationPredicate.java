// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.ExecutionException;
import com.newrelic.agent.deps.com.google.common.cache.CacheLoader;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.cache.LoadingCache;

public class DefaultDestinationPredicate implements DestinationPredicate
{
    private static final long MAX_CACHE_SIZE_BUFFER = 200L;
    private final RootConfigAttributesNode mandatoryExcludeTrie;
    private final RootConfigAttributesNode configTrie;
    private final AttributesNode defaultExcludeTrie;
    private final LoadingCache<String, Boolean> cache;
    private final String destination;
    
    public DefaultDestinationPredicate(final String dest, final Set<String> exclude, final Set<String> include, final Set<String> defaultExcludes, final Set<String> mandatoryExclude) {
        this.mandatoryExcludeTrie = generateExcludeConfigTrie(dest, mandatoryExclude);
        this.configTrie = generateConfigTrie(dest, exclude, include);
        this.defaultExcludeTrie = generateDefaultTrie(dest, defaultExcludes);
        this.destination = dest;
        this.cache = CacheBuilder.newBuilder().maximumSize(200L).build((CacheLoader<? super String, Boolean>)new CacheLoader<String, Boolean>() {
            public Boolean load(final String key) throws Exception {
                return DefaultDestinationPredicate.this.isIncluded(key);
            }
        });
    }
    
    private Boolean isIncluded(final String key) {
        Boolean output = this.mandatoryExcludeTrie.applyRules(key);
        if (output == null) {
            output = this.configTrie.applyRules(key);
        }
        if (output == null) {
            output = this.defaultExcludeTrie.applyRules(key);
        }
        return output;
    }
    
    public boolean apply(final String key) {
        try {
            return this.changeToPrimitiveAndLog(key, this.cache.get(key));
        }
        catch (ExecutionException e) {
            return this.changeToPrimitiveAndLog(key, this.isIncluded(key));
        }
    }
    
    private void logOutput(final String key, final boolean value) {
        if (Agent.LOG.isFineEnabled()) {
            Agent.LOG.log(Level.FINER, "{0}: Attribute {1} is {2}", new Object[] { this.destination, key, value ? "enabled" : "disabled" });
        }
    }
    
    private boolean changeToPrimitiveAndLog(final String key, final Boolean value) {
        final boolean out = value == null || value;
        this.logOutput(key, out);
        return out;
    }
    
    public boolean isPotentialConfigMatch(final String key) {
        final List<AttributesNode> queue = new LinkedList<AttributesNode>();
        queue.addAll(this.configTrie.getChildren());
        while (!queue.isEmpty()) {
            final AttributesNode node = queue.remove(0);
            queue.addAll(node.getChildren());
            if (node.isIncludeDestination() && node.mightMatch(key)) {
                return true;
            }
        }
        return false;
    }
    
    protected static AttributesNode generateDefaultTrie(final String dest, final Set<String> defaultExcludes) {
        final AttributesNode root = new AttributesNode("*", true, dest, true);
        for (final String current : defaultExcludes) {
            root.addNode(new AttributesNode(current, false, dest, true));
        }
        return root;
    }
    
    protected static RootConfigAttributesNode generateExcludeConfigTrie(final String dest, final Set<String> exclude) {
        final RootConfigAttributesNode root = new RootConfigAttributesNode(dest);
        addSpecifcInOrEx(root, false, exclude, dest, true);
        return root;
    }
    
    protected static RootConfigAttributesNode generateConfigTrie(final String dest, final Set<String> exclude, final Set<String> include) {
        final RootConfigAttributesNode root = new RootConfigAttributesNode(dest);
        addSpecifcInOrEx(root, false, exclude, dest, false);
        addSpecifcInOrEx(root, true, include, dest, false);
        return root;
    }
    
    private static void addSpecifcInOrEx(final AttributesNode root, final boolean isInclude, final Set<String> inOrEx, final String dest, final boolean isDefault) {
        for (final String current : inOrEx) {
            root.addNode(new AttributesNode(current, isInclude, dest, isDefault));
        }
    }
}
