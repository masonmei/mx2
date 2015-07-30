// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface TrustStrategy
{
    boolean isTrusted(X509Certificate[] p0, String p1) throws CertificateException;
}
