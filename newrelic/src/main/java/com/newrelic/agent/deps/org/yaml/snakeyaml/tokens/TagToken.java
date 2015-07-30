// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.tokens;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class TagToken extends Token
{
    private final String[] value;
    
    public TagToken(final String[] value, final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        if (value.length != 2) {
            throw new YAMLException("Two strings must be provided instead of " + String.valueOf(value.length));
        }
        this.value = value;
    }
    
    public String[] getValue() {
        return this.value;
    }
    
    protected String getArguments() {
        return "value=[" + this.value[0] + ", " + this.value[1] + "]";
    }
    
    public String getTokenId() {
        return "<tag>";
    }
}
