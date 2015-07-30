// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.classic.net.SocketAppender;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class ConsolePluginAction extends Action
{
    private static final String PORT_ATTR = "port";
    private static final Integer DEFAULT_PORT;
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) throws ActionException {
        final String portStr = attributes.getValue("port");
        Integer port = null;
        if (portStr == null) {
            port = ConsolePluginAction.DEFAULT_PORT;
        }
        else {
            try {
                port = Integer.valueOf(portStr);
            }
            catch (NumberFormatException ex) {
                this.addError("Port " + portStr + " in ConsolePlugin config is not a correct number");
            }
        }
        final LoggerContext lc = (LoggerContext)ec.getContext();
        final SocketAppender appender = new SocketAppender();
        appender.setContext(lc);
        appender.setIncludeCallerData(true);
        appender.setRemoteHost("localhost");
        appender.setPort(port);
        appender.start();
        final Logger root = lc.getLogger("ROOT");
        root.addAppender(appender);
        this.addInfo("Sending LoggingEvents to the plugin using port " + port);
    }
    
    public void end(final InterpretationContext ec, final String name) throws ActionException {
    }
    
    static {
        DEFAULT_PORT = 4321;
    }
}
