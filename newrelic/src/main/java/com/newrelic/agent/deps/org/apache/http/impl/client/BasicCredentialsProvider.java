// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import java.util.Iterator;
import java.util.Map;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.auth.Credentials;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScope;
import java.util.concurrent.ConcurrentHashMap;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.client.CredentialsProvider;

@ThreadSafe
public class BasicCredentialsProvider implements CredentialsProvider
{
    private final ConcurrentHashMap<AuthScope, Credentials> credMap;
    
    public BasicCredentialsProvider() {
        this.credMap = new ConcurrentHashMap<AuthScope, Credentials>();
    }
    
    public void setCredentials(final AuthScope authscope, final Credentials credentials) {
        Args.notNull(authscope, "Authentication scope");
        this.credMap.put(authscope, credentials);
    }
    
    private static Credentials matchCredentials(final Map<AuthScope, Credentials> map, final AuthScope authscope) {
        Credentials creds = map.get(authscope);
        if (creds == null) {
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (final AuthScope current : map.keySet()) {
                final int factor = authscope.match(current);
                if (factor > bestMatchFactor) {
                    bestMatchFactor = factor;
                    bestMatch = current;
                }
            }
            if (bestMatch != null) {
                creds = map.get(bestMatch);
            }
        }
        return creds;
    }
    
    public Credentials getCredentials(final AuthScope authscope) {
        Args.notNull(authscope, "Authentication scope");
        return matchCredentials(this.credMap, authscope);
    }
    
    public void clear() {
        this.credMap.clear();
    }
    
    public String toString() {
        return this.credMap.toString();
    }
}
