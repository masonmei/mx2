// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.message;

import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.StatusLine;
import com.newrelic.agent.deps.org.apache.http.RequestLine;
import com.newrelic.agent.deps.org.apache.http.ParseException;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;

public interface LineParser
{
    ProtocolVersion parseProtocolVersion(CharArrayBuffer p0, ParserCursor p1) throws ParseException;
    
    boolean hasProtocolVersion(CharArrayBuffer p0, ParserCursor p1);
    
    RequestLine parseRequestLine(CharArrayBuffer p0, ParserCursor p1) throws ParseException;
    
    StatusLine parseStatusLine(CharArrayBuffer p0, ParserCursor p1) throws ParseException;
    
    Header parseHeader(CharArrayBuffer p0) throws ParseException;
}
