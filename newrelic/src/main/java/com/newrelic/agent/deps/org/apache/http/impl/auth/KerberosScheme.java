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
public class KerberosScheme extends GGSSchemeBase
{
    private static final String KERBEROS_OID = "1.2.840.113554.1.2.2";
    
    public KerberosScheme(final boolean stripPort) {
        super(stripPort);
    }
    
    public KerberosScheme() {
        super(false);
    }
    
    public String getSchemeName() {
        return "Kerberos";
    }
    
    public Header authenticate(final Credentials credentials, final HttpRequest request, final HttpContext context) throws AuthenticationException {
        return super.authenticate(credentials, request, context);
    }
    
    protected byte[] generateToken(final byte[] input, final String authServer) throws GSSException {
        return this.generateGSSToken(input, new Oid("1.2.840.113554.1.2.2"), authServer);
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
