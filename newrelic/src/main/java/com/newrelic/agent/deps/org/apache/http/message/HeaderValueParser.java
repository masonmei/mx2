// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.message;

import com.newrelic.agent.deps.org.apache.http.NameValuePair;
import com.newrelic.agent.deps.org.apache.http.ParseException;
import com.newrelic.agent.deps.org.apache.http.HeaderElement;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;

public interface HeaderValueParser
{
    HeaderElement[] parseElements(CharArrayBuffer p0, ParserCursor p1) throws ParseException;
    
    HeaderElement parseHeaderElement(CharArrayBuffer p0, ParserCursor p1) throws ParseException;
    
    NameValuePair[] parseParameters(CharArrayBuffer p0, ParserCursor p1) throws ParseException;
    
    NameValuePair parseNameValuePair(CharArrayBuffer p0, ParserCursor p1) throws ParseException;
}
