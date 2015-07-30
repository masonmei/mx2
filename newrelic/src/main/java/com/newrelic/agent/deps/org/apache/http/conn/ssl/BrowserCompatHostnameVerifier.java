// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class BrowserCompatHostnameVerifier extends AbstractVerifier
{
    public final void verify(final String host, final String[] cns, final String[] subjectAlts) throws SSLException {
        this.verify(host, cns, subjectAlts, false);
    }
    
    boolean validCountryWildcard(final String cn) {
        return true;
    }
    
    public final String toString() {
        return "BROWSER_COMPATIBLE";
    }
}
