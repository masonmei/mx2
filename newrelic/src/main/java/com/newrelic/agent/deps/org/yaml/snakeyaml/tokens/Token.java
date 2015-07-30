// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.tokens;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public abstract class Token
{
    private final Mark startMark;
    private final Mark endMark;
    
    public Token(final Mark startMark, final Mark endMark) {
        assert startMark != null;
        assert endMark != null;
        this.startMark = startMark;
        this.endMark = endMark;
    }
    
    public String toString() {
        return "<" + this.getClass().getName() + "(" + this.getArguments() + ")>";
    }
    
    public Mark getStartMark() {
        return this.startMark;
    }
    
    public Mark getEndMark() {
        return this.endMark;
    }
    
    protected String getArguments() {
        return "";
    }
    
    public abstract String getTokenId();
    
    public boolean equals(final Object obj) {
        return obj instanceof Token && this.toString().equals(obj.toString());
    }
}
