// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.auth.Credentials;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScope;

public interface CredentialsProvider
{
    void setCredentials(AuthScope p0, Credentials p1);
    
    Credentials getCredentials(AuthScope p0);
    
    void clear();
}
