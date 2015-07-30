// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

public class ServerProp
{
    private final Object value;
    
    private ServerProp(final Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public static ServerProp createPropObject(final Object value) {
        return new ServerProp(value);
    }
}
