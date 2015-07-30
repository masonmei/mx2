// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.ch.qos.logback.classic.selector.ContextSelector;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.Discriminator;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class JNDIBasedContextDiscriminator extends ContextAwareBase implements Discriminator<ILoggingEvent>
{
    private static final String KEY = "contextName";
    private String defaultValue;
    private boolean started;
    
    public JNDIBasedContextDiscriminator() {
        this.started = false;
    }
    
    public String getDiscriminatingValue(final ILoggingEvent event) {
        final ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
        if (selector == null) {
            return this.defaultValue;
        }
        final LoggerContext lc = selector.getLoggerContext();
        if (lc == null) {
            return this.defaultValue;
        }
        return lc.getName();
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
