// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class StrictHostnameVerifier extends AbstractVerifier
{
    public final void verify(final String host, final String[] cns, final String[] subjectAlts) throws SSLException {
        this.verify(host, cns, subjectAlts, true);
    }
    
    public final String toString() {
        return "STRICT";
    }
}
