// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.sift;

import com.newrelic.agent.deps.ch.qos.logback.core.helpers.NOPAppender;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;

public abstract class SiftingAppenderBase<E> extends AppenderBase<E>
{
    protected AppenderTracker<E> appenderTracker;
    AppenderFactoryBase<E> appenderFactory;
    Discriminator<E> discriminator;
    int nopaWarningCount;
    
    public SiftingAppenderBase() {
        this.appenderTracker = new AppenderTrackerImpl<E>();
        this.nopaWarningCount = 0;
    }
    
    public void setAppenderFactory(final AppenderFactoryBase<E> appenderFactory) {
        this.appenderFactory = appenderFactory;
    }
    
    public void start() {
        int errors = 0;
        if (this.discriminator == null) {
            this.addError("Missing discriminator. Aborting");
            ++errors;
        }
        if (!this.discriminator.isStarted()) {
            this.addError("Discriminator has not started successfully. Aborting");
            ++errors;
        }
        if (errors == 0) {
            super.start();
        }
    }
    
    public void stop() {
        for (final Appender<E> appender : this.appenderTracker.valueList()) {
            appender.stop();
        }
    }
    
    protected abstract long getTimestamp(final E p0);
    
    protected void append(final E event) {
        if (!this.isStarted()) {
            return;
        }
        final String discriminatingValue = this.discriminator.getDiscriminatingValue(event);
        final long timestamp = this.getTimestamp(event);
        Appender<E> appender = this.appenderTracker.get(discriminatingValue, timestamp);
        if (appender == null) {
            try {
                appender = this.appenderFactory.buildAppender(this.context, discriminatingValue);
                if (appender == null) {
                    appender = this.buildNOPAppender(discriminatingValue);
                }
                this.appenderTracker.put(discriminatingValue, appender, timestamp);
            }
            catch (JoranException e) {
                this.addError("Failed to build appender for [" + discriminatingValue + "]", e);
                return;
            }
        }
        this.appenderTracker.stopStaleAppenders(timestamp);
        appender.doAppend(event);
    }
    
    public Discriminator<E> getDiscriminator() {
        return this.discriminator;
    }
    
    public void setDiscriminator(final Discriminator<E> discriminator) {
        this.discriminator = discriminator;
    }
    
    NOPAppender<E> buildNOPAppender(final String discriminatingValue) {
        if (this.nopaWarningCount < 4) {
            ++this.nopaWarningCount;
            this.addError("Failed to build an appender for discriminating value [" + discriminatingValue + "]");
        }
        final NOPAppender<E> nopa = new NOPAppender<E>();
        nopa.setContext(this.context);
        nopa.start();
        return nopa;
    }
    
    public AppenderTracker getAppenderTracker() {
        return this.appenderTracker;
    }
    
    public String getDiscriminatorKey() {
        if (this.discriminator != null) {
            return this.discriminator.getKey();
        }
        return null;
    }
}
