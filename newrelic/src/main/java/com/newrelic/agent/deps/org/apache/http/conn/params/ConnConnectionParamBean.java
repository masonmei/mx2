// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.params;

import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
public class ConnConnectionParamBean extends HttpAbstractParamBean
{
    public ConnConnectionParamBean(final HttpParams params) {
        super(params);
    }
    
    @Deprecated
    public void setMaxStatusLineGarbage(final int maxStatusLineGarbage) {
        this.params.setIntParameter("http.connection.max-status-line-garbage", maxStatusLineGarbage);
    }
}
