// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.commons;

import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "com/newrelic/agent/deps/org/apache/commons/httpclient/protocol/Protocol" })
public interface Protocol
{
    String getScheme();
}
