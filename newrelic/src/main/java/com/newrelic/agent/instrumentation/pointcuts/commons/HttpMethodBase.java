// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.commons;

import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "com/newrelic/agent/deps/org/apache/commons/httpclient/HttpMethodBase" })
public interface HttpMethodBase
{
    void setRequestHeader(String p0, String p1);
}
