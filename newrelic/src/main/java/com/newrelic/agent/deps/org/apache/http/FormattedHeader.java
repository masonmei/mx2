// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;

public interface FormattedHeader extends Header
{
    CharArrayBuffer getBuffer();
    
    int getValuePos();
}
