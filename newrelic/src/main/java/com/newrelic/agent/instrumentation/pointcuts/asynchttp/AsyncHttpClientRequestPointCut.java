// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.asynchttp;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import java.net.MalformedURLException;
import java.net.URL;
import com.newrelic.agent.instrumentation.pointcuts.scala.ScalaTracerHolder;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class AsyncHttpClientRequestPointCut extends PointCut implements EntryInvocationHandler
{
    public static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    
    public AsyncHttpClientRequestPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(AsyncHttpClientRequestPointCut.POINT_CUT_NAME, "play2_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new InterfaceMatcher("com/ning/http/client/AsyncHttpProvider");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new NameMethodMatcher("execute");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Transaction tx = Transaction.getTransaction();
        if (!tx.isStarted()) {
            return;
        }
        Request request = null;
        if (args[0] instanceof Request) {
            request = (Request)args[0];
            ScalaTracerHolder tracerHolder = null;
            if (args[1] instanceof AsyncHandler) {
                final Object oref = ((AsyncHandler)args[1])._nr_objectRef();
                if (oref instanceof ObjectRef) {
                    final Object elem = ((ObjectRef)oref)._nr_element();
                    if (elem instanceof ScalaTracerHolder) {
                        tracerHolder = (ScalaTracerHolder)elem;
                    }
                }
            }
            else {
                if (!(args[1] instanceof JavaAsyncHandler)) {
                    return;
                }
                tracerHolder = (ScalaTracerHolder)((JavaAsyncHandler)args[1])._nr_scalaPromise();
            }
            URL url = null;
            try {
                url = new URL(request.getUrl());
            }
            catch (MalformedURLException ex) {}
            if (tracerHolder != null) {
                final Object tracerInfo = new AsyncHttpClientTracerInfo(sig, (url == null) ? "URL_PARSE_ERROR" : url.getHost(), request.getUrl(), request.getMethod());
                tracerHolder._nr_setTracer(tracerInfo);
            }
        }
    }
    
    static {
        POINT_CUT_NAME = AsyncHttpClientRequestPointCut.class.getName();
    }
    
    public static final class AsyncHttpClientTracerInfo
    {
        private final long startTime;
        private final ClassMethodSignature sig;
        private final String host;
        private final String uri;
        private final String methodName;
        
        public AsyncHttpClientTracerInfo(final ClassMethodSignature sig, final String host, final String uri, final String methodName) {
            this.startTime = System.nanoTime();
            this.sig = sig;
            this.host = host;
            this.uri = uri;
            this.methodName = methodName;
        }
        
        public long getStartTime() {
            return this.startTime;
        }
        
        public ClassMethodSignature getClassMethodSignature() {
            return this.sig;
        }
        
        public String getHost() {
            return this.host;
        }
        
        public String getUri() {
            return this.uri;
        }
        
        public String getMethodName() {
            return this.methodName;
        }
    }
    
    @InterfaceMixin(originalClassName = { "scala/runtime/ObjectRef" })
    public interface ObjectRef
    {
        public static final String CLASS = "scala/runtime/ObjectRef";
        
        @FieldAccessor(fieldName = "elem", existingField = true)
        Object _nr_element();
    }
    
    @InterfaceMixin(originalClassName = { "play.api.libs.ws.WS$WSRequest$$anon$1", "play.api.libs.ws.ning.NingWSRequest$$anon$1" })
    public interface AsyncHandler
    {
        public static final String SCALA_CLASS = "play.api.libs.ws.WS$WSRequest$$anon$1";
        public static final String SCALA_CLASS_2_3 = "play.api.libs.ws.ning.NingWSRequest$$anon$1";
        
        @FieldAccessor(fieldName = "result$1", fieldDesc = "Lscala/runtime/ObjectRef;", existingField = true)
        Object _nr_objectRef();
    }
    
    @InterfaceMixin(originalClassName = { "play.libs.WS$WSRequest$1", "play.libs.ws.ning.NingWSRequest$1" })
    public interface JavaAsyncHandler
    {
        public static final String CLASS = "play.libs.WS$WSRequest$1";
        public static final String CLASS_2_3 = "play.libs.ws.ning.NingWSRequest$1";
        
        @FieldAccessor(fieldName = "val$scalaPromise", fieldDesc = "Lscala/concurrent/Promise;", existingField = true)
        Object _nr_scalaPromise();
    }
    
    @InterfaceMixin(originalClassName = { "com/ning/http/client/Request" })
    public interface Request
    {
        public static final String INTERFACE = "com/ning/http/client/Request";
        
        String getMethod();
        
        String getUrl();
    }
}
