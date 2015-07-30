// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.recovery;

import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.InfoStatus;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.io.OutputStream;

public abstract class ResilientOutputStreamBase extends OutputStream
{
    static final int STATUS_COUNT_LIMIT = 8;
    private int noContextWarning;
    private int statusCount;
    private Context context;
    private RecoveryCoordinator recoveryCoordinator;
    protected OutputStream os;
    protected boolean presumedClean;
    
    public ResilientOutputStreamBase() {
        this.noContextWarning = 0;
        this.statusCount = 0;
        this.presumedClean = true;
    }
    
    private boolean isPresumedInError() {
        return this.recoveryCoordinator != null && !this.presumedClean;
    }
    
    public void write(final byte[] b, final int off, final int len) {
        if (this.isPresumedInError()) {
            if (!this.recoveryCoordinator.isTooSoon()) {
                this.attemptRecovery();
            }
            return;
        }
        try {
            this.os.write(b, off, len);
            this.postSuccessfulWrite();
        }
        catch (IOException e) {
            this.postIOFailure(e);
        }
    }
    
    public void write(final int b) {
        if (this.isPresumedInError()) {
            if (!this.recoveryCoordinator.isTooSoon()) {
                this.attemptRecovery();
            }
            return;
        }
        try {
            this.os.write(b);
            this.postSuccessfulWrite();
        }
        catch (IOException e) {
            this.postIOFailure(e);
        }
    }
    
    public void flush() {
        if (this.os != null) {
            try {
                this.os.flush();
                this.postSuccessfulWrite();
            }
            catch (IOException e) {
                this.postIOFailure(e);
            }
        }
    }
    
    abstract String getDescription();
    
    abstract OutputStream openNewOutputStream() throws IOException;
    
    private void postSuccessfulWrite() {
        if (this.recoveryCoordinator != null) {
            this.recoveryCoordinator = null;
            this.statusCount = 0;
            this.addStatus(new InfoStatus("Recovered from IO failure on " + this.getDescription(), this));
        }
    }
    
    void postIOFailure(final IOException e) {
        this.addStatusIfCountNotOverLimit(new ErrorStatus("IO failure while writing to " + this.getDescription(), this, e));
        this.presumedClean = false;
        if (this.recoveryCoordinator == null) {
            this.recoveryCoordinator = new RecoveryCoordinator();
        }
    }
    
    public void close() throws IOException {
        if (this.os != null) {
            this.os.close();
        }
    }
    
    void attemptRecovery() {
        try {
            this.close();
        }
        catch (IOException ex) {}
        this.addStatusIfCountNotOverLimit(new InfoStatus("Attempting to recover from IO failure on " + this.getDescription(), this));
        try {
            this.os = this.openNewOutputStream();
            this.presumedClean = true;
        }
        catch (IOException e) {
            this.addStatusIfCountNotOverLimit(new ErrorStatus("Failed to open " + this.getDescription(), this, e));
        }
    }
    
    void addStatusIfCountNotOverLimit(final Status s) {
        ++this.statusCount;
        if (this.statusCount < 8) {
            this.addStatus(s);
        }
        if (this.statusCount == 8) {
            this.addStatus(s);
            this.addStatus(new InfoStatus("Will supress future messages regarding " + this.getDescription(), this));
        }
    }
    
    public void addStatus(final Status status) {
        if (this.context == null) {
            if (this.noContextWarning++ == 0) {
                System.out.println("LOGBACK: No context given for " + this);
            }
            return;
        }
        final StatusManager sm = this.context.getStatusManager();
        if (sm != null) {
            sm.add(status);
        }
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public void setContext(final Context context) {
        this.context = context;
    }
}
