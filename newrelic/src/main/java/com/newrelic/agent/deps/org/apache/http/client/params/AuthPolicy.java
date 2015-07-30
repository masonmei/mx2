// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.params;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Deprecated
@Immutable
public final class AuthPolicy
{
    public static final String NTLM = "NTLM";
    public static final String DIGEST = "Digest";
    public static final String BASIC = "Basic";
    public static final String SPNEGO = "negotiate";
    public static final String KERBEROS = "Kerberos";
}
