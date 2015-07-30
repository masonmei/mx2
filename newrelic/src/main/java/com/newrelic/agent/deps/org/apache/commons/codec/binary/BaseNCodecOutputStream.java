// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.binary;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class BaseNCodecOutputStream extends FilterOutputStream
{
    private final boolean doEncode;
    private final BaseNCodec baseNCodec;
    private final byte[] singleByte;
    
    public BaseNCodecOutputStream(final OutputStream out, final BaseNCodec basedCodec, final boolean doEncode) {
        super(out);
        this.singleByte = new byte[1];
        this.baseNCodec = basedCodec;
        this.doEncode = doEncode;
    }
    
    public void write(final int i) throws IOException {
        this.singleByte[0] = (byte)i;
        this.write(this.singleByte, 0, 1);
    }
    
    public void write(final byte[] b, final int offset, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > b.length || offset + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > 0) {
            if (this.doEncode) {
                this.baseNCodec.encode(b, offset, len);
            }
            else {
                this.baseNCodec.decode(b, offset, len);
            }
            this.flush(false);
        }
    }
    
    private void flush(final boolean propogate) throws IOException {
        final int avail = this.baseNCodec.available();
        if (avail > 0) {
            final byte[] buf = new byte[avail];
            final int c = this.baseNCodec.readResults(buf, 0, avail);
            if (c > 0) {
                this.out.write(buf, 0, c);
            }
        }
        if (propogate) {
            this.out.flush();
        }
    }
    
    public void flush() throws IOException {
        this.flush(true);
    }
    
    public void close() throws IOException {
        if (this.doEncode) {
            this.baseNCodec.encode(this.singleByte, 0, -1);
        }
        else {
            this.baseNCodec.decode(this.singleByte, 0, -1);
        }
        this.flush();
        this.out.close();
    }
}
