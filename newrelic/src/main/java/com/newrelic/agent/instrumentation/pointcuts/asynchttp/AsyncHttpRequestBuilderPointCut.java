// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.asynchttp;

import com.newrelic.agent.instrumentation.pointcuts.MethodMapper;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class AsyncHttpRequestBuilderPointCut extends PointCut implements EntryInvocationHandler
{
    public static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    
    public AsyncHttpRequestBuilderPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(AsyncHttpRequestBuilderPointCut.POINT_CUT_NAME, "play2_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("com/ning/http/client/RequestBuilderBase");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("build", "()Lcom/ning/http/client/Request;");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Transaction tx = Transaction.getTransaction();
        if (!tx.isStarted()) {
            return;
        }
        RequestBuilder request = null;
        if (object instanceof RequestBuilder) {
            request = (RequestBuilder)object;
            tx.getCrossProcessState().processOutboundRequestHeaders((OutboundHeaders)new OutboundHeadersWrapper(request));
        }
    }
    
    static {
        POINT_CUT_NAME = AsyncHttpRequestBuilderPointCut.class.getName();
    }
    
    private class OutboundHeadersWrapper implements OutboundHeaders
    {
        private final RequestBuilder request;
        
        public OutboundHeadersWrapper(final RequestBuilder connection) {
            this.request = connection;
        }
        
        public void setHeader(final String name, final String value) {
            this.request.nr_setHeader(name, value);
        }
        
        public HeaderType getHeaderType() {
            return HeaderType.HTTP;
        }
    }
    
    @InterfaceMapper(originalInterfaceName = "com/ning/http/client/RequestBuilderBase")
    public interface RequestBuilder
    {
        public static final String CLASS = "com/ning/http/client/RequestBuilderBase";
        
        @MethodMapper(originalMethodName = "setHeader", originalDescriptor = "(Ljava/lang/String;Ljava/lang/String;)Lcom/ning/http/client/RequestBuilderBase;", invokeInterface = false)
        Object nr_setHeader(String p0, String p1);
    }
}
