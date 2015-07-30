// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.net;

import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.SyslogStartConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.Layout;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.IThrowableProxy;
import java.io.IOException;
import java.io.OutputStream;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.LevelToSyslogSeverity;
import com.newrelic.agent.deps.ch.qos.logback.classic.PatternLayout;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.net.SyslogAppenderBase;

public class SyslogAppender extends SyslogAppenderBase<ILoggingEvent>
{
    public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
    public static final String DEFAULT_STACKTRACE_PATTERN = "\t";
    PatternLayout stackTraceLayout;
    String stackTracePattern;
    boolean throwableExcluded;
    
    public SyslogAppender() {
        this.stackTraceLayout = new PatternLayout();
        this.stackTracePattern = "\t";
        this.throwableExcluded = false;
    }
    
    public void start() {
        super.start();
        this.setupStackTraceLayout();
    }
    
    String getPrefixPattern() {
        return "%syslogStart{" + this.getFacility() + "}%nopex";
    }
    
    public int getSeverityForEvent(final Object eventObject) {
        final ILoggingEvent event = (ILoggingEvent)eventObject;
        return LevelToSyslogSeverity.convert(event);
    }
    
    protected void postProcess(final Object eventObject, final OutputStream sw) {
        if (this.throwableExcluded) {
            return;
        }
        final ILoggingEvent event = (ILoggingEvent)eventObject;
        IThrowableProxy tp = event.getThrowableProxy();
        if (tp == null) {
            return;
        }
        final String stackTracePrefix = this.stackTraceLayout.doLayout(event);
        boolean isRootException = true;
        while (tp != null) {
            final StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
            try {
                this.handleThrowableFirstLine(sw, tp, stackTracePrefix, isRootException);
                isRootException = false;
                for (final StackTraceElementProxy step : stepArray) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(stackTracePrefix).append(step);
                    sw.write(sb.toString().getBytes());
                    sw.flush();
                }
            }
            catch (IOException e) {
                break;
            }
            tp = tp.getCause();
        }
    }
    
    private void handleThrowableFirstLine(final OutputStream sw, final IThrowableProxy tp, final String stackTracePrefix, final boolean isRootException) throws IOException {
        final StringBuilder sb = new StringBuilder().append(stackTracePrefix);
        if (!isRootException) {
            sb.append("Caused by: ");
        }
        sb.append(tp.getClassName()).append(": ").append(tp.getMessage());
        sw.write(sb.toString().getBytes());
        sw.flush();
    }
    
    boolean stackTraceHeaderLine(final StringBuilder sb, final boolean topException) {
        return false;
    }
    
    public Layout<ILoggingEvent> buildLayout() {
        final PatternLayout layout = new PatternLayout();
        layout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        if (this.suffixPattern == null) {
            this.suffixPattern = "[%thread] %logger %msg";
        }
        layout.setPattern(this.getPrefixPattern() + this.suffixPattern);
        layout.setContext(this.getContext());
        layout.start();
        return layout;
    }
    
    private void setupStackTraceLayout() {
        this.stackTraceLayout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        this.stackTraceLayout.setPattern(this.getPrefixPattern() + this.stackTracePattern);
        this.stackTraceLayout.setContext(this.getContext());
        this.stackTraceLayout.start();
    }
    
    public boolean isThrowableExcluded() {
        return this.throwableExcluded;
    }
    
    public void setThrowableExcluded(final boolean throwableExcluded) {
        this.throwableExcluded = throwableExcluded;
    }
    
    public String getStackTracePattern() {
        return this.stackTracePattern;
    }
    
    public void setStackTracePattern(final String stackTracePattern) {
        this.stackTracePattern = stackTracePattern;
    }
}
