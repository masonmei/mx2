// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.CompressionMode;
import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.FileAppender;

public class RollingFileAppender<E> extends FileAppender<E>
{
    File currentlyActiveFile;
    TriggeringPolicy<E> triggeringPolicy;
    RollingPolicy rollingPolicy;
    
    public void start() {
        if (this.triggeringPolicy == null) {
            this.addWarn("No TriggeringPolicy was set for the RollingFileAppender named " + this.getName());
            this.addWarn("For more information, please visit http://logback.qos.ch/codes.html#rfa_no_tp");
            return;
        }
        if (!this.append) {
            this.addWarn("Append mode is mandatory for RollingFileAppender");
            this.append = true;
        }
        if (this.rollingPolicy == null) {
            this.addError("No RollingPolicy was set for the RollingFileAppender named " + this.getName());
            this.addError("For more information, please visit http://logback.qos.ch/codes.htmlrfa_no_rp");
            return;
        }
        if (this.isPrudent()) {
            if (this.rawFileProperty() != null) {
                this.addWarn("Setting \"File\" property to null on account of prudent mode");
                this.setFile(null);
            }
            if (this.rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
                this.addError("Compression is not supported in prudent mode. Aborting");
                return;
            }
        }
        this.currentlyActiveFile = new File(this.getFile());
        this.addInfo("Active log file name: " + this.getFile());
        super.start();
    }
    
    public void stop() {
        if (this.rollingPolicy != null) {
            this.rollingPolicy.stop();
        }
        if (this.triggeringPolicy != null) {
            this.triggeringPolicy.stop();
        }
        super.stop();
    }
    
    public void setFile(final String file) {
        if (file != null && (this.triggeringPolicy != null || this.rollingPolicy != null)) {
            this.addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
            this.addError("Visit http://logback.qos.ch/codes.html#rfa_file_after for more information");
        }
        super.setFile(file);
    }
    
    public String getFile() {
        return this.rollingPolicy.getActiveFileName();
    }
    
    public void rollover() {
        synchronized (this.lock) {
            this.closeOutputStream();
            try {
                this.rollingPolicy.rollover();
            }
            catch (RolloverFailure rf) {
                this.addWarn("RolloverFailure occurred. Deferring roll-over.");
                this.append = true;
            }
            try {
                this.currentlyActiveFile = new File(this.rollingPolicy.getActiveFileName());
                this.openFile(this.rollingPolicy.getActiveFileName());
            }
            catch (IOException e) {
                this.addError("setFile(" + this.fileName + ", false) call failed.", e);
            }
        }
    }
    
    protected void subAppend(final E event) {
        synchronized (this.triggeringPolicy) {
            if (this.triggeringPolicy.isTriggeringEvent(this.currentlyActiveFile, event)) {
                this.rollover();
            }
        }
        super.subAppend(event);
    }
    
    public RollingPolicy getRollingPolicy() {
        return this.rollingPolicy;
    }
    
    public TriggeringPolicy<E> getTriggeringPolicy() {
        return this.triggeringPolicy;
    }
    
    public void setRollingPolicy(final RollingPolicy policy) {
        this.rollingPolicy = policy;
        if (this.rollingPolicy instanceof TriggeringPolicy) {
            this.triggeringPolicy = (TriggeringPolicy<E>)policy;
        }
    }
    
    public void setTriggeringPolicy(final TriggeringPolicy<E> policy) {
        this.triggeringPolicy = policy;
        if (policy instanceof RollingPolicy) {
            this.rollingPolicy = (RollingPolicy)policy;
        }
    }
}
