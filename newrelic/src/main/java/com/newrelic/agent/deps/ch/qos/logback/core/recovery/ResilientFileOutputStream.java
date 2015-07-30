// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.recovery;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class ResilientFileOutputStream extends ResilientOutputStreamBase
{
    private File file;
    private FileOutputStream fos;
    
    public ResilientFileOutputStream(final File file, final boolean append) throws FileNotFoundException {
        this.file = file;
        this.fos = new FileOutputStream(file, append);
        this.os = new BufferedOutputStream(this.fos);
        this.presumedClean = true;
    }
    
    public FileChannel getChannel() {
        if (this.os == null) {
            return null;
        }
        return this.fos.getChannel();
    }
    
    public File getFile() {
        return this.file;
    }
    
    String getDescription() {
        return "file [" + this.file + "]";
    }
    
    OutputStream openNewOutputStream() throws IOException {
        this.fos = new FileOutputStream(this.file, true);
        return new BufferedOutputStream(this.fos);
    }
    
    public String toString() {
        return "c.q.l.c.recovery.ResilientFileOutputStream@" + System.identityHashCode(this);
    }
}
