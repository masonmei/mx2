// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.netty;

import java.util.Map;
import java.util.List;
import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "org/jboss/netty/handler/codec/http/DefaultHttpResponse" })
public interface NettyHttpResponse
{
    @FieldAccessor(fieldName = "status", fieldDesc = "Lorg/jboss/netty/handler/codec/http/HttpResponseStatus;", existingField = true)
    HttpResponseStatus _nr_status();
    
    List<String> getHeaders(String p0);
    
    List<Map.Entry<String, String>> getHeaders();
    
    void setHeader(String p0, Object p1);
    
    @InterfaceMixin(originalClassName = { "org/jboss/netty/handler/codec/http/HttpResponseStatus" })
    public interface HttpResponseStatus
    {
        public static final String CLASS = "org/jboss/netty/handler/codec/http/HttpResponseStatus";
        
        int getCode();
        
        String getReasonPhrase();
    }
}
