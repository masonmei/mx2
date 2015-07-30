// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.tokens;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class ScalarToken extends Token
{
    private final String value;
    private final boolean plain;
    private final char style;
    
    public ScalarToken(final String value, final Mark startMark, final Mark endMark, final boolean plain) {
        this(value, plain, startMark, endMark, '\0');
    }
    
    public ScalarToken(final String value, final boolean plain, final Mark startMark, final Mark endMark, final char style) {
        super(startMark, endMark);
        this.value = value;
        this.plain = plain;
        this.style = style;
    }
    
    public boolean getPlain() {
        return this.plain;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public char getStyle() {
        return this.style;
    }
    
    protected String getArguments() {
        return "value=" + this.value + ", plain=" + this.plain + ", style=" + this.style;
    }
    
    public String getTokenId() {
        return "<scalar>";
    }
}
