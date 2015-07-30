// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import com.newrelic.agent.deps.ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.AppenderFactoryBase;

public class AppenderFactory extends AppenderFactoryBase<ILoggingEvent>
{
    String key;
    
    AppenderFactory(final List<SaxEvent> eventList, final String key) {
        super(eventList);
        this.key = key;
    }
    
    public SiftingJoranConfiguratorBase<ILoggingEvent> getSiftingJoranConfigurator(final String discriminatingValue) {
        return new SiftingJoranConfigurator(this.key, discriminatingValue);
    }
}
