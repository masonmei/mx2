// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.tokens;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class AnchorToken extends Token
{
    private final String value;
    
    public AnchorToken(final String value, final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    protected String getArguments() {
        return "value=" + this.value;
    }
    
    public String getTokenId() {
        return "<anchor>";
    }
}
