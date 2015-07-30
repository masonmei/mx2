// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class NonClosableInputStream extends FilterInputStream
{
    NonClosableInputStream(final InputStream is) {
        super(is);
    }
    
    public void close() {
    }
    
    public void realClose() throws IOException {
        super.close();
    }
}
