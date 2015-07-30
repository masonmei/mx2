// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.netty;

import java.util.Map;
import java.util.List;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "org/jboss/netty/handler/codec/http/DefaultHttpRequest" })
public interface NettyHttpRequest
{
    String getUri();
    
    List<String> getHeaders(String p0);
    
    List<Map.Entry<String, String>> getHeaders();
}
