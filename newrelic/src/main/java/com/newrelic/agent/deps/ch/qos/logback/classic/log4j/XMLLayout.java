// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.log4j;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.IThrowableProxy;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.helpers.Transform;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.LayoutBase;

public class XMLLayout extends LayoutBase<ILoggingEvent>
{
    private final int DEFAULT_SIZE = 256;
    private final int UPPER_LIMIT = 2048;
    private StringBuilder buf;
    private boolean locationInfo;
    private boolean properties;
    
    public XMLLayout() {
        this.buf = new StringBuilder(256);
        this.locationInfo = false;
        this.properties = false;
    }
    
    public void start() {
        super.start();
    }
    
    public void setLocationInfo(final boolean flag) {
        this.locationInfo = flag;
    }
    
    public boolean getLocationInfo() {
        return this.locationInfo;
    }
    
    public void setProperties(final boolean flag) {
        this.properties = flag;
    }
    
    public boolean getProperties() {
        return this.properties;
    }
    
    public String doLayout(final ILoggingEvent event) {
        if (this.buf.capacity() > 2048) {
            this.buf = new StringBuilder(256);
        }
        else {
            this.buf.setLength(0);
        }
        this.buf.append("<log4j:event logger=\"");
        this.buf.append(event.getLoggerName());
        this.buf.append("\"\r\n");
        this.buf.append("             timestamp=\"");
        this.buf.append(event.getTimeStamp());
        this.buf.append("\" level=\"");
        this.buf.append(event.getLevel());
        this.buf.append("\" thread=\"");
        this.buf.append(event.getThreadName());
        this.buf.append("\">\r\n");
        this.buf.append("  <log4j:message><![CDATA[");
        Transform.appendEscapingCDATA(this.buf, event.getFormattedMessage());
        this.buf.append("]]></log4j:message>\r\n");
        final IThrowableProxy tp = event.getThrowableProxy();
        if (tp != null) {
            final StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
            this.buf.append("  <log4j:throwable><![CDATA[");
            for (final StackTraceElementProxy step : stepArray) {
                this.buf.append('\t');
                this.buf.append(step.toString());
                this.buf.append("\r\n");
            }
            this.buf.append("]]></log4j:throwable>\r\n");
        }
        if (this.locationInfo) {
            final StackTraceElement[] callerDataArray = event.getCallerData();
            if (callerDataArray != null && callerDataArray.length > 0) {
                final StackTraceElement immediateCallerData = callerDataArray[0];
                this.buf.append("  <log4j:locationInfo class=\"");
                this.buf.append(immediateCallerData.getClassName());
                this.buf.append("\"\r\n");
                this.buf.append("                      method=\"");
                this.buf.append(Transform.escapeTags(immediateCallerData.getMethodName()));
                this.buf.append("\" file=\"");
                this.buf.append(immediateCallerData.getFileName());
                this.buf.append("\" line=\"");
                this.buf.append(immediateCallerData.getLineNumber());
                this.buf.append("\"/>\r\n");
            }
        }
        if (this.getProperties()) {
            final Map<String, String> propertyMap = event.getMDCPropertyMap();
            if (propertyMap != null && propertyMap.size() != 0) {
                final Set<Map.Entry<String, String>> entrySet = propertyMap.entrySet();
                this.buf.append("  <log4j:properties>");
                for (final Map.Entry<String, String> entry : entrySet) {
                    this.buf.append("\r\n    <log4j:data");
                    this.buf.append(" name='" + Transform.escapeTags(entry.getKey()) + "'");
                    this.buf.append(" value='" + Transform.escapeTags(entry.getValue()) + "'");
                    this.buf.append(" />");
                }
                this.buf.append("\r\n  </log4j:properties>");
            }
        }
        this.buf.append("\r\n</log4j:event>\r\n\r\n");
        return this.buf.toString();
    }
    
    public String getContentType() {
        return "text/xml";
    }
}
