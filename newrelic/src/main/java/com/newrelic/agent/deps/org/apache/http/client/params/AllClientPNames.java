// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.params;

import com.newrelic.agent.deps.org.apache.http.conn.params.ConnRoutePNames;
import com.newrelic.agent.deps.org.apache.http.conn.params.ConnManagerPNames;
import com.newrelic.agent.deps.org.apache.http.conn.params.ConnConnectionPNames;
import com.newrelic.agent.deps.org.apache.http.cookie.params.CookieSpecPNames;
import com.newrelic.agent.deps.org.apache.http.auth.params.AuthPNames;
import com.newrelic.agent.deps.org.apache.http.params.CoreProtocolPNames;
import com.newrelic.agent.deps.org.apache.http.params.CoreConnectionPNames;

@Deprecated
public interface AllClientPNames extends CoreConnectionPNames, CoreProtocolPNames, ClientPNames, AuthPNames, CookieSpecPNames, ConnConnectionPNames, ConnManagerPNames, ConnRoutePNames
{
}
