// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import java.io.OutputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.util.EnvUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.WarnStatus;
import java.util.Arrays;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ConsoleTarget;

public class ConsoleAppender<E> extends OutputStreamAppender<E>
{
    protected ConsoleTarget target;
    protected boolean withJansi;
    private static final String WindowsAnsiOutputStream_CLASS_NAME = "org.fusesource.jansi.WindowsAnsiOutputStream";
    
    public ConsoleAppender() {
        this.target = ConsoleTarget.SystemOut;
        this.withJansi = false;
    }
    
    public void setTarget(final String value) {
        final ConsoleTarget t = ConsoleTarget.findByName(value.trim());
        if (t == null) {
            this.targetWarn(value);
        }
        else {
            this.target = t;
        }
    }
    
    public String getTarget() {
        return this.target.getName();
    }
    
    private void targetWarn(final String val) {
        final Status status = new WarnStatus("[" + val + "] should be one of " + Arrays.toString(ConsoleTarget.values()), this);
        status.add(new WarnStatus("Using previously set target, System.out by default.", this));
        this.addStatus(status);
    }
    
    public void start() {
        OutputStream targetStream = this.target.getStream();
        if (EnvUtil.isWindows() && this.withJansi) {
            targetStream = this.getTargetStreamForWindows(targetStream);
        }
        this.setOutputStream(targetStream);
        super.start();
    }
    
    private OutputStream getTargetStreamForWindows(final OutputStream targetStream) {
        try {
            this.addInfo("Enabling JANSI WindowsAnsiOutputStream for the console.");
            final Object windowsAnsiOutputStream = OptionHelper.instantiateByClassNameAndParameter("org.fusesource.jansi.WindowsAnsiOutputStream", Object.class, this.context, OutputStream.class, targetStream);
            return (OutputStream)windowsAnsiOutputStream;
        }
        catch (Exception e) {
            this.addWarn("Failed to create WindowsAnsiOutputStream. Falling back on the default stream.", e);
            return targetStream;
        }
    }
    
    public boolean isWithJansi() {
        return this.withJansi;
    }
    
    public void setWithJansi(final boolean withJansi) {
        this.withJansi = withJansi;
    }
}
