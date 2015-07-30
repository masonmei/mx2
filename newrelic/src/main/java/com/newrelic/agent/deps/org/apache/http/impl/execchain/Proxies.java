// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.execchain;

import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.HttpEntityEnclosingRequest;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
class Proxies
{
    static void enhanceEntity(final HttpEntityEnclosingRequest request) {
        final HttpEntity entity = request.getEntity();
        if (entity != null && !entity.isRepeatable() && !isEnhanced(entity)) {
            final HttpEntity proxy = (HttpEntity)Proxy.newProxyInstance(HttpEntity.class.getClassLoader(), new Class[] { HttpEntity.class }, new RequestEntityExecHandler(entity));
            request.setEntity(proxy);
        }
    }
    
    static boolean isEnhanced(final HttpEntity entity) {
        if (entity != null && Proxy.isProxyClass(entity.getClass())) {
            final InvocationHandler handler = Proxy.getInvocationHandler(entity);
            return handler instanceof RequestEntityExecHandler;
        }
        return false;
    }
    
    static boolean isRepeatable(final HttpRequest request) {
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null) {
                if (isEnhanced(entity)) {
                    final RequestEntityExecHandler handler = (RequestEntityExecHandler)Proxy.getInvocationHandler(entity);
                    if (!handler.isConsumed()) {
                        return true;
                    }
                }
                return entity.isRepeatable();
            }
        }
        return true;
    }
    
    public static CloseableHttpResponse enhanceResponse(final HttpResponse original, final ConnectionHolder connHolder) {
        return (CloseableHttpResponse)Proxy.newProxyInstance(ResponseProxyHandler.class.getClassLoader(), new Class[] { CloseableHttpResponse.class }, new ResponseProxyHandler(original, connHolder));
    }
}
