// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.io.IOException;
import java.io.OutputStream;

public enum ConsoleTarget
{
    SystemOut("System.out", (OutputStream)new OutputStream() {
        public void write(final int b) throws IOException {
            System.out.write(b);
        }
        
        public void write(final byte[] b) throws IOException {
            System.out.write(b);
        }
        
        public void write(final byte[] b, final int off, final int len) throws IOException {
            System.out.write(b, off, len);
        }
        
        public void flush() throws IOException {
            System.out.flush();
        }
    }), 
    SystemErr("System.err", (OutputStream)new OutputStream() {
        public void write(final int b) throws IOException {
            System.err.write(b);
        }
        
        public void write(final byte[] b) throws IOException {
            System.err.write(b);
        }
        
        public void write(final byte[] b, final int off, final int len) throws IOException {
            System.err.write(b, off, len);
        }
        
        public void flush() throws IOException {
            System.err.flush();
        }
    });
    
    private final String name;
    private final OutputStream stream;
    
    public static ConsoleTarget findByName(final String name) {
        for (final ConsoleTarget target : values()) {
            if (target.name.equalsIgnoreCase(name)) {
                return target;
            }
        }
        return null;
    }
    
    private ConsoleTarget(final String name, final OutputStream stream) {
        this.name = name;
        this.stream = stream;
    }
    
    public String getName() {
        return this.name;
    }
    
    public OutputStream getStream() {
        return this.stream;
    }
}
