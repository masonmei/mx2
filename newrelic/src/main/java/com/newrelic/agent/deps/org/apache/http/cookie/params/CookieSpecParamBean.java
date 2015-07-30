// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.cookie.params;

import java.util.Collection;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
@NotThreadSafe
public class CookieSpecParamBean extends HttpAbstractParamBean
{
    public CookieSpecParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setDatePatterns(final Collection<String> patterns) {
        this.params.setParameter("http.protocol.cookie-datepatterns", patterns);
    }
    
    public void setSingleHeader(final boolean singleHeader) {
        this.params.setBooleanParameter("http.protocol.single-cookie-header", singleHeader);
    }
}
