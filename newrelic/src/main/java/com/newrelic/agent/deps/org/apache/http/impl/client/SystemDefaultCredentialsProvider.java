// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.net.PasswordAuthentication;
import com.newrelic.agent.deps.org.apache.http.auth.UsernamePasswordCredentials;
import java.net.InetAddress;
import java.net.Authenticator;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.auth.Credentials;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScope;
import java.util.Map;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.client.CredentialsProvider;

@ThreadSafe
public class SystemDefaultCredentialsProvider implements CredentialsProvider
{
    private static final Map<String, String> SCHEME_MAP;
    private final BasicCredentialsProvider internal;
    
    private static String translateScheme(final String key) {
        if (key == null) {
            return null;
        }
        final String s = SystemDefaultCredentialsProvider.SCHEME_MAP.get(key);
        return (s != null) ? s : key;
    }
    
    public SystemDefaultCredentialsProvider() {
        this.internal = new BasicCredentialsProvider();
    }
    
    public void setCredentials(final AuthScope authscope, final Credentials credentials) {
        this.internal.setCredentials(authscope, credentials);
    }
    
    public Credentials getCredentials(final AuthScope authscope) {
        Args.notNull(authscope, "Auth scope");
        final Credentials localcreds = this.internal.getCredentials(authscope);
        if (localcreds != null) {
            return localcreds;
        }
        if (authscope.getHost() != null) {
            final PasswordAuthentication systemcreds = Authenticator.requestPasswordAuthentication(authscope.getHost(), null, authscope.getPort(), "http", null, translateScheme(authscope.getScheme()));
            if (systemcreds != null) {
                return new UsernamePasswordCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()));
            }
        }
        return null;
    }
    
    public void clear() {
        this.internal.clear();
    }
    
    static {
        (SCHEME_MAP = new ConcurrentHashMap<String, String>()).put("Basic".toUpperCase(Locale.ENGLISH), "Basic");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Digest".toUpperCase(Locale.ENGLISH), "Digest");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("NTLM".toUpperCase(Locale.ENGLISH), "NTLM");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("negotiate".toUpperCase(Locale.ENGLISH), "SPNEGO");
        SystemDefaultCredentialsProvider.SCHEME_MAP.put("Kerberos".toUpperCase(Locale.ENGLISH), "Kerberos");
    }
}
