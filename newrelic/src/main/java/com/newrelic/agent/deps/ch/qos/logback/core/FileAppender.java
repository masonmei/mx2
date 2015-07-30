// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.io.OutputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.recovery.ResilientFileOutputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.util.FileUtil;
import java.io.File;
import java.io.IOException;

public class FileAppender<E> extends OutputStreamAppender<E>
{
    protected boolean append;
    protected String fileName;
    private boolean prudent;
    
    public FileAppender() {
        this.append = true;
        this.fileName = null;
        this.prudent = false;
    }
    
    public void setFile(final String file) {
        if (file == null) {
            this.fileName = file;
        }
        else {
            this.fileName = file.trim();
        }
    }
    
    public boolean isAppend() {
        return this.append;
    }
    
    public final String rawFileProperty() {
        return this.fileName;
    }
    
    public String getFile() {
        return this.fileName;
    }
    
    public void start() {
        int errors = 0;
        if (this.getFile() != null) {
            this.addInfo("File property is set to [" + this.fileName + "]");
            if (this.prudent && !this.isAppend()) {
                this.setAppend(true);
                this.addWarn("Setting \"Append\" property to true on account of \"Prudent\" mode");
            }
            try {
                this.openFile(this.getFile());
            }
            catch (IOException e) {
                ++errors;
                this.addError("openFile(" + this.fileName + "," + this.append + ") call failed.", e);
            }
        }
        else {
            ++errors;
            this.addError("\"File\" property not set for appender named [" + this.name + "].");
        }
        if (errors == 0) {
            super.start();
        }
    }
    
    public void openFile(final String file_name) throws IOException {
        synchronized (this.lock) {
            final File file = new File(file_name);
            if (FileUtil.isParentDirectoryCreationRequired(file)) {
                final boolean result = FileUtil.createMissingParentDirectories(file);
                if (!result) {
                    this.addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
                }
            }
            final ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, this.append);
            resilientFos.setContext(this.context);
            this.setOutputStream(resilientFos);
        }
    }
    
    public boolean isPrudent() {
        return this.prudent;
    }
    
    public void setPrudent(final boolean prudent) {
        this.prudent = prudent;
    }
    
    public void setAppend(final boolean append) {
        this.append = append;
    }
    
    private void safeWrite(final E event) throws IOException {
        final ResilientFileOutputStream resilientFOS = (ResilientFileOutputStream)this.getOutputStream();
        final FileChannel fileChannel = resilientFOS.getChannel();
        if (fileChannel == null) {
            return;
        }
        FileLock fileLock = null;
        try {
            fileLock = fileChannel.lock();
            final long position = fileChannel.position();
            final long size = fileChannel.size();
            if (size != position) {
                fileChannel.position(size);
            }
            super.writeOut(event);
        }
        finally {
            if (fileLock != null) {
                fileLock.release();
            }
        }
    }
    
    protected void writeOut(final E event) throws IOException {
        if (this.prudent) {
            this.safeWrite(event);
        }
        else {
            super.writeOut(event);
        }
    }
}
