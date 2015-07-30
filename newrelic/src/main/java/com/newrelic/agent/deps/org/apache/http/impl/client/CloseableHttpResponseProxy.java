// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import java.lang.reflect.Proxy;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.util.EntityUtils;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import java.lang.reflect.InvocationHandler;

@NotThreadSafe
class CloseableHttpResponseProxy implements InvocationHandler
{
    private final HttpResponse original;
    
    CloseableHttpResponseProxy(final HttpResponse original) {
        this.original = original;
    }
    
    public void close() throws IOException {
        final HttpEntity entity = this.original.getEntity();
        EntityUtils.consume(entity);
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String mname = method.getName();
        if (mname.equals("close")) {
            this.close();
            return null;
        }
        try {
            return method.invoke(this.original, args);
        }
        catch (InvocationTargetException ex) {
            final Throwable cause = ex.getCause();
            if (cause != null) {
                throw cause;
            }
            throw ex;
        }
    }
    
    public static CloseableHttpResponse newProxy(final HttpResponse original) {
        return (CloseableHttpResponse)Proxy.newProxyInstance(CloseableHttpResponseProxy.class.getClassLoader(), new Class[] { CloseableHttpResponse.class }, new CloseableHttpResponseProxy(original));
    }
}
