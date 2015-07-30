// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.net;

import java.net.InetAddress;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PreSerializationTransformer;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.net.SocketAppenderBase;

public class SocketAppender extends SocketAppenderBase<ILoggingEvent>
{
    boolean includeCallerData;
    PreSerializationTransformer<ILoggingEvent> pst;
    
    public SocketAppender() {
        this.includeCallerData = false;
        this.pst = new LoggingEventPreSerializationTransformer();
    }
    
    public SocketAppender(final InetAddress address, final int port) {
        this.includeCallerData = false;
        this.pst = new LoggingEventPreSerializationTransformer();
        this.address = address;
        this.remoteHost = address.getHostName();
        this.port = port;
    }
    
    public SocketAppender(final String host, final int port) {
        this.includeCallerData = false;
        this.pst = new LoggingEventPreSerializationTransformer();
        this.port = port;
        this.address = SocketAppenderBase.getAddressByName(host);
        this.remoteHost = host;
    }
    
    protected void postProcessEvent(final ILoggingEvent event) {
        if (this.includeCallerData) {
            event.getCallerData();
        }
    }
    
    public void setIncludeCallerData(final boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }
    
    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return this.pst;
    }
}
