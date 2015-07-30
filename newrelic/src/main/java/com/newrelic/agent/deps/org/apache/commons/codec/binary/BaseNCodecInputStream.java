// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class BaseNCodecInputStream extends FilterInputStream
{
    private final boolean doEncode;
    private final BaseNCodec baseNCodec;
    private final byte[] singleByte;
    
    protected BaseNCodecInputStream(final InputStream in, final BaseNCodec baseNCodec, final boolean doEncode) {
        super(in);
        this.singleByte = new byte[1];
        this.doEncode = doEncode;
        this.baseNCodec = baseNCodec;
    }
    
    public int read() throws IOException {
        int r;
        for (r = this.read(this.singleByte, 0, 1); r == 0; r = this.read(this.singleByte, 0, 1)) {}
        if (r > 0) {
            return (this.singleByte[0] < 0) ? (256 + this.singleByte[0]) : this.singleByte[0];
        }
        return -1;
    }
    
    public int read(final byte[] b, final int offset, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > b.length || offset + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int readLen;
        for (readLen = 0; readLen == 0; readLen = this.baseNCodec.readResults(b, offset, len)) {
            if (!this.baseNCodec.hasData()) {
                final byte[] buf = new byte[this.doEncode ? 4096 : 8192];
                final int c = this.in.read(buf);
                if (this.doEncode) {
                    this.baseNCodec.encode(buf, 0, c);
                }
                else {
                    this.baseNCodec.decode(buf, 0, c);
                }
            }
        }
        return readLen;
    }
    
    public boolean markSupported() {
        return false;
    }
}
