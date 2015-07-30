// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.commons;

import com.newrelic.agent.instrumentation.pointcuts.MethodMapper;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.tracers.ExternalComponentPointCut;

public abstract class HttpCommonsPointCut extends ExternalComponentPointCut
{
    public HttpCommonsPointCut(final Class<? extends HttpCommonsPointCut> pointCutClass, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(new PointCutConfiguration(pointCutClass), classMatcher, methodMatcher);
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String uri, final String methodName) {
        return new HttpCommonsTracer(transaction, sig, object, host, "CommonsHttp", uri, methodName);
    }
    
    @InterfaceMixin(originalClassName = { "com/newrelic/agent/deps/org/apache/http/StatusLine" })
    public interface StatusLine
    {
        int getStatusCode();
    }
    
    @InterfaceMapper(originalInterfaceName = "com/newrelic/agent/deps/org/apache/http/message/BasicHttpResponse", className = { "com/newrelic/agent/deps/org/apache/http/message/BasicHttpResponse" })
    public interface BasicHttpResponseExtension
    {
        @MethodMapper(originalMethodName = "getStatusLine", originalDescriptor = "()Lorg/apache/http/StatusLine;", invokeInterface = false)
        Object _nr_getStatusLine();
    }
}
