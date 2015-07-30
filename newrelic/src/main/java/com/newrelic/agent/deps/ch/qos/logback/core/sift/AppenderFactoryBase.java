// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.sift;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.util.Collection;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

public abstract class AppenderFactoryBase<E>
{
    final List<SaxEvent> eventList;
    
    protected AppenderFactoryBase(final List<SaxEvent> eventList) {
        this.eventList = new ArrayList<SaxEvent>(eventList);
        this.removeSiftElement();
    }
    
    void removeSiftElement() {
        this.eventList.remove(0);
        this.eventList.remove(this.eventList.size() - 1);
    }
    
    public abstract SiftingJoranConfiguratorBase<E> getSiftingJoranConfigurator(final String p0);
    
    Appender<E> buildAppender(final Context context, final String discriminatingValue) throws JoranException {
        final SiftingJoranConfiguratorBase<E> sjc = this.getSiftingJoranConfigurator(discriminatingValue);
        sjc.setContext(context);
        sjc.doConfigure(this.eventList);
        return sjc.getAppender();
    }
    
    public List<SaxEvent> getEventList() {
        return this.eventList;
    }
}
