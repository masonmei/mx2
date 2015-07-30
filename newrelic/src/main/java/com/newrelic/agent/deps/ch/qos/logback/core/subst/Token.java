// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.subst;

public class Token
{
    public static final Token START_TOKEN;
    public static final Token CURLY_LEFT_TOKEN;
    public static final Token CURLY_RIGHT_TOKEN;
    public static final Token DEFAULT_SEP_TOKEN;
    Type type;
    String payload;
    
    public Token(final Type type, final String payload) {
        this.type = type;
        this.payload = payload;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Token token = (Token)o;
        if (this.type != token.type) {
            return false;
        }
        if (this.payload != null) {
            if (this.payload.equals(token.payload)) {
                return true;
            }
        }
        else if (token.payload == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = (this.type != null) ? this.type.hashCode() : 0;
        result = 31 * result + ((this.payload != null) ? this.payload.hashCode() : 0);
        return result;
    }
    
    public String toString() {
        String result = "Token{type=" + this.type;
        if (this.payload != null) {
            result = result + ", payload='" + this.payload + '\'';
        }
        result += '}';
        return result;
    }
    
    static {
        START_TOKEN = new Token(Type.START, null);
        CURLY_LEFT_TOKEN = new Token(Type.CURLY_LEFT, null);
        CURLY_RIGHT_TOKEN = new Token(Type.CURLY_RIGHT, null);
        DEFAULT_SEP_TOKEN = new Token(Type.DEFAULT, null);
    }
    
    public enum Type
    {
        LITERAL, 
        START, 
        CURLY_LEFT, 
        CURLY_RIGHT, 
        DEFAULT;
    }
}
