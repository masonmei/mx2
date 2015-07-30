// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.auth;

import com.newrelic.agent.deps.org.apache.http.util.Args;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import com.newrelic.agent.deps.org.apache.http.auth.AuthenticationException;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.auth.Credentials;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class SPNegoScheme extends GGSSchemeBase
{
    private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
    
    public SPNegoScheme(final boolean stripPort) {
        super(stripPort);
    }
    
    public SPNegoScheme() {
        super(false);
    }
    
    public String getSchemeName() {
        return "Negotiate";
    }
    
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        return super.authenticate(credentials, request, context);
    }
    
    protected byte[] generateToken(final byte[] input, final String authServer) throws GSSException {
        return this.generateGSSToken(input, new Oid("1.3.6.1.5.5.2"), authServer);
    }
    
    public String getParameter(final String name) {
        Args.notNull(name, "Parameter name");
        return null;
    }
    
    public String getRealm() {
        return null;
    }
    
    public boolean isConnectionBased() {
        return true;
    }
}
