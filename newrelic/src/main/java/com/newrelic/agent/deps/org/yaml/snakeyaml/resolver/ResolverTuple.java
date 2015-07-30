// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.resolver;

import java.util.regex.Pattern;

final class ResolverTuple
{
    private final String tag;
    private final Pattern regexp;
    
    public ResolverTuple(final String tag, final Pattern regexp) {
        this.tag = tag;
        this.regexp = regexp;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public Pattern getRegexp() {
        return this.regexp;
    }
    
    public String toString() {
        return "Tuple tag=" + this.tag + " regexp=" + this.regexp;
    }
}
