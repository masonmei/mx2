// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.tokens;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.List;

public final class DirectiveToken extends Token
{
    private final String name;
    private final List<?> value;
    
    public DirectiveToken(final String name, final List<?> value, final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        this.name = name;
        if (value != null && value.size() != 2) {
            throw new YAMLException("Two strings must be provided instead of " + String.valueOf(value.size()));
        }
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<?> getValue() {
        return this.value;
    }
    
    protected String getArguments() {
        if (this.value != null) {
            return "name=" + this.name + ", value=[" + this.value.get(0) + ", " + this.value.get(1) + "]";
        }
        return "name=" + this.name;
    }
    
    public String getTokenId() {
        return "<directive>";
    }
}
