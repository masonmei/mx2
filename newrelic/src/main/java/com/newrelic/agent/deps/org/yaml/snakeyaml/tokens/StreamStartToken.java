// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.tokens;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class StreamStartToken extends Token
{
    public StreamStartToken(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
    
    public String getTokenId() {
        return "<stream start>";
    }
}
