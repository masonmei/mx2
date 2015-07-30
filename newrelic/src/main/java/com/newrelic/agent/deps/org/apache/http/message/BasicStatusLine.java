// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.message;

import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import java.io.Serializable;
import com.newrelic.agent.deps.org.apache.http.StatusLine;

@Immutable
public class BasicStatusLine implements StatusLine, Cloneable, Serializable
{
    private static final long serialVersionUID = -2443303766890459269L;
    private final ProtocolVersion protoVersion;
    private final int statusCode;
    private final String reasonPhrase;
    
    public BasicStatusLine(final ProtocolVersion version, final int statusCode, final String reasonPhrase) {
        this.protoVersion = Args.notNull(version, "Version");
        this.statusCode = Args.notNegative(statusCode, "Status code");
        this.reasonPhrase = reasonPhrase;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public ProtocolVersion getProtocolVersion() {
        return this.protoVersion;
    }
    
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
    
    public String toString() {
        return BasicLineFormatter.INSTANCE.formatStatusLine(null, this).toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
