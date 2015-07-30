// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Closeable;

public interface JarResource extends Closeable
{
    InputStream getInputStream(String p0) throws IOException;
    
    long getSize(String p0);
}
