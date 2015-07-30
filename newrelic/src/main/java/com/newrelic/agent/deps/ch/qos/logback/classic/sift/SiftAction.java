// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.AppenderFactoryBase;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.InPlayListener;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class SiftAction extends Action implements InPlayListener
{
    List<SaxEvent> seList;
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) throws ActionException {
        this.seList = new ArrayList<SaxEvent>();
        ec.addInPlayListener(this);
    }
    
    public void end(final InterpretationContext ec, final String name) throws ActionException {
        ec.removeInPlayListener(this);
        final Object o = ec.peekObject();
        if (o instanceof SiftingAppender) {
            final SiftingAppender sa = (SiftingAppender)o;
            final AppenderFactory appenderFactory = new AppenderFactory(this.seList, sa.getDiscriminatorKey());
            sa.setAppenderFactory(appenderFactory);
        }
    }
    
    public void inPlay(final SaxEvent event) {
        this.seList.add(event);
    }
    
    public List<SaxEvent> getSeList() {
        return this.seList;
    }
}
