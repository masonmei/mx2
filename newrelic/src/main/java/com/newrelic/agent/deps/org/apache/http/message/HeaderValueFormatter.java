// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.message;

import com.newrelic.agent.deps.org.apache.http.NameValuePair;
import com.newrelic.agent.deps.org.apache.http.HeaderElement;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;

public interface HeaderValueFormatter
{
    CharArrayBuffer formatElements(CharArrayBuffer p0, HeaderElement[] p1, boolean p2);
    
    CharArrayBuffer formatHeaderElement(CharArrayBuffer p0, HeaderElement p1, boolean p2);
    
    CharArrayBuffer formatParameters(CharArrayBuffer p0, NameValuePair[] p1, boolean p2);
    
    CharArrayBuffer formatNameValuePair(CharArrayBuffer p0, NameValuePair p1, boolean p2);
}
