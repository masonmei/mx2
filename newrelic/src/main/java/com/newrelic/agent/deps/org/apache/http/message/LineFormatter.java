// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.message;

import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.StatusLine;
import com.newrelic.agent.deps.org.apache.http.RequestLine;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;

public interface LineFormatter
{
    CharArrayBuffer appendProtocolVersion(CharArrayBuffer p0, ProtocolVersion p1);
    
    CharArrayBuffer formatRequestLine(CharArrayBuffer p0, RequestLine p1);
    
    CharArrayBuffer formatStatusLine(CharArrayBuffer p0, StatusLine p1);
    
    CharArrayBuffer formatHeader(CharArrayBuffer p0, Header p1);
}
