// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.message;

import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import java.io.Serializable;
import com.newrelic.agent.deps.org.apache.http.RequestLine;

@Immutable
public class BasicRequestLine implements RequestLine, Cloneable, Serializable
{
    private static final long serialVersionUID = 2810581718468737193L;
    private final ProtocolVersion protoversion;
    private final String method;
    private final String uri;
    
    public BasicRequestLine(final String method, final String uri, final ProtocolVersion version) {
        this.method = Args.notNull(method, "Method");
        this.uri = Args.notNull(uri, "URI");
        this.protoversion = Args.notNull(version, "Version");
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public ProtocolVersion getProtocolVersion() {
        return this.protoversion;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public String toString() {
        return BasicLineFormatter.INSTANCE.formatRequestLine(null, this).toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
