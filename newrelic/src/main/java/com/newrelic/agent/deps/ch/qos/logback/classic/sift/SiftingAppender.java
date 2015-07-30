// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.DefaultClass;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.Discriminator;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.SiftingAppenderBase;

public class SiftingAppender extends SiftingAppenderBase<ILoggingEvent>
{
    protected long getTimestamp(final ILoggingEvent event) {
        return event.getTimeStamp();
    }
    
    @DefaultClass(MDCBasedDiscriminator.class)
    public void setDiscriminator(final Discriminator<ILoggingEvent> discriminator) {
        super.setDiscriminator(discriminator);
    }
}
