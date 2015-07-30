// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.commons;

import com.newrelic.agent.instrumentation.pointcuts.MethodMapper;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;

@InterfaceMapper(originalInterfaceName = "com/newrelic/agent/deps/org/apache/commons/httpclient/HttpMethod", className = { "com/newrelic/agent/deps/org/apache/commons/httpclient/HttpMethodBase" })
public interface HttpMethodExtension
{
    @MethodMapper(originalMethodName = "getRequestHeader", originalDescriptor = "(Ljava/lang/String;)Lorg/apache/commons/httpclient/Header;", invokeInterface = false)
    Object _nr_getRequestHeader(String p0);
    
    @MethodMapper(originalMethodName = "getResponseHeader", originalDescriptor = "(Ljava/lang/String;)Lorg/apache/commons/httpclient/Header;", invokeInterface = false)
    Object _nr_getResponseHeader(String p0);
    
    @MethodMapper(originalMethodName = "getURI", originalDescriptor = "()Lorg/apache/commons/httpclient/URI;", invokeInterface = false)
    Object _nr_getUri();
}
