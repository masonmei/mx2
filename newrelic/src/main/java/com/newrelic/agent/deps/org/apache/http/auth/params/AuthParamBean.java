// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.auth.params;

import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class AuthParamBean extends HttpAbstractParamBean
{
    public AuthParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setCredentialCharset(final String charset) {
        AuthParams.setCredentialCharset(this.params, charset);
    }
}
