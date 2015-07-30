// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.io.OutputStream;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;

public class EchoEncoder<E> extends EncoderBase<E>
{
    String fileHeader;
    String fileFooter;
    
    public void doEncode(final E event) throws IOException {
        final String val = event + CoreConstants.LINE_SEPARATOR;
        this.outputStream.write(val.getBytes());
        this.outputStream.flush();
    }
    
    public void close() throws IOException {
        if (this.fileFooter == null) {
            return;
        }
        this.outputStream.write(this.fileFooter.getBytes());
    }
    
    public void init(final OutputStream os) throws IOException {
        super.init(os);
        if (this.fileHeader == null) {
            return;
        }
        this.outputStream.write(this.fileHeader.getBytes());
    }
}
