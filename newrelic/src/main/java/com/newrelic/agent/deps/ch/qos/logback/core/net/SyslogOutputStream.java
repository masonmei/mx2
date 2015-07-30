// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.net;

import java.net.DatagramPacket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.ByteArrayOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.OutputStream;

public class SyslogOutputStream extends OutputStream
{
    private static final int MAX_LEN = 1024;
    private InetAddress address;
    private DatagramSocket ds;
    private ByteArrayOutputStream baos;
    private final int port;
    
    public SyslogOutputStream(final String syslogHost, final int port) throws UnknownHostException, SocketException {
        this.baos = new ByteArrayOutputStream();
        this.address = InetAddress.getByName(syslogHost);
        this.port = port;
        this.ds = new DatagramSocket();
    }
    
    public void write(final byte[] byteArray, final int offset, final int len) throws IOException {
        this.baos.write(byteArray, offset, len);
    }
    
    public void flush() throws IOException {
        final byte[] bytes = this.baos.toByteArray();
        final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.address, this.port);
        if (this.baos.size() > 1024) {
            this.baos = new ByteArrayOutputStream();
        }
        else {
            this.baos.reset();
        }
        if (bytes.length == 0) {
            return;
        }
        if (this.ds != null) {
            this.ds.send(packet);
        }
    }
    
    public void close() {
        this.address = null;
        this.ds = null;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void write(final int b) throws IOException {
        this.baos.write(b);
    }
}
