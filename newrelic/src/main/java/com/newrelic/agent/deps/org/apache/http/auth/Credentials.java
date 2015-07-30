// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.auth;

import java.security.Principal;

public interface Credentials
{
    Principal getUserPrincipal();
    
    String getPassword();
}
