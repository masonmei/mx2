// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.sift;

public class DefaultDiscriminator<E> implements Discriminator<E>
{
    public static final String DEFAULT = "default";
    boolean started;
    
    public DefaultDiscriminator() {
        this.started = false;
    }
    
    public String getDiscriminatingValue(final E e) {
        return "default";
    }
    
    public String getKey() {
        return "default";
    }
    
    public void start() {
        this.started = true;
    }
    
    public void stop() {
        this.started = false;
    }
    
    public boolean isStarted() {
        return this.started;
    }
}
