// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.Discriminator;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class ContextBasedDiscriminator extends ContextAwareBase implements Discriminator<ILoggingEvent>
{
    private static final String KEY = "contextName";
    private String defaultValue;
    private boolean started;
    
    public ContextBasedDiscriminator() {
        this.started = false;
    }
    
    public String getDiscriminatingValue(final ILoggingEvent event) {
        final String contextName = event.getLoggerContextVO().getName();
        if (contextName == null) {
            return this.defaultValue;
        }
        return contextName;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void start() {
        this.started = true;
    }
    
    public void stop() {
        this.started = false;
    }
    
    public String getKey() {
        return "contextName";
    }
    
    public void setKey(final String key) {
        throw new UnsupportedOperationException("Key cannot be set. Using fixed key contextName");
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
