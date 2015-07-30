// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import java.util.Date;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.LevelToSyslogSeverity;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import java.text.DateFormatSymbols;
import java.util.Locale;
import com.newrelic.agent.deps.ch.qos.logback.core.net.SyslogAppenderBase;
import java.text.SimpleDateFormat;

public class SyslogStartConverter extends ClassicConverter
{
    long lastTimestamp;
    String timesmapStr;
    SimpleDateFormat simpleFormat;
    String localHostName;
    int facility;
    
    public SyslogStartConverter() {
        this.lastTimestamp = -1L;
        this.timesmapStr = null;
    }
    
    public void start() {
        int errorCount = 0;
        final String facilityStr = this.getFirstOption();
        if (facilityStr == null) {
            this.addError("was expecting a facility string as an option");
            return;
        }
        this.facility = SyslogAppenderBase.facilityStringToint(facilityStr);
        this.localHostName = this.getLocalHostname();
        try {
            this.simpleFormat = new SimpleDateFormat("MMM dd HH:mm:ss", new DateFormatSymbols(Locale.US));
        }
        catch (IllegalArgumentException e) {
            this.addError("Could not instantiate SimpleDateFormat", e);
            ++errorCount;
        }
        if (errorCount == 0) {
            super.start();
        }
    }
    
    public String convert(final ILoggingEvent event) {
        final StringBuilder sb = new StringBuilder();
        final int pri = this.facility + LevelToSyslogSeverity.convert(event);
        sb.append("<");
        sb.append(pri);
        sb.append(">");
        sb.append(this.computeTimeStampString(event.getTimeStamp()));
        sb.append(' ');
        sb.append(this.localHostName);
        sb.append(' ');
        return sb.toString();
    }
    
    public String getLocalHostname() {
        try {
            final InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        }
        catch (UnknownHostException uhe) {
            this.addError("Could not determine local host name", uhe);
            return "UNKNOWN_LOCALHOST";
        }
    }
    
    String computeTimeStampString(final long now) {
        synchronized (this) {
            if (now != this.lastTimestamp) {
                this.lastTimestamp = now;
                this.timesmapStr = this.simpleFormat.format(new Date(now));
            }
            return this.timesmapStr;
        }
    }
}
