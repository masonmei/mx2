// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import java.security.cert.X509Certificate;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.HostnameVerifier;

public interface X509HostnameVerifier extends HostnameVerifier
{
    void verify(String p0, SSLSocket p1) throws IOException;
    
    void verify(String p0, X509Certificate p1) throws SSLException;
    
    void verify(String p0, String[] p1, String[] p2) throws SSLException;
}
