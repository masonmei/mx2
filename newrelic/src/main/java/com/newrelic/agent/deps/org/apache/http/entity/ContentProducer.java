// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.entity;

import java.io.IOException;
import java.io.OutputStream;

public interface ContentProducer
{
    void writeTo(OutputStream p0) throws IOException;
}
