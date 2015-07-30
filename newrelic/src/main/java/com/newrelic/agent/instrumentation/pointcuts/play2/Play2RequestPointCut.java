// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play2;

import com.newrelic.agent.instrumentation.pointcuts.MethodMapper;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;
import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import java.util.Iterator;
import com.newrelic.api.agent.Request;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import com.newrelic.agent.instrumentation.pointcuts.scala.ScalaCollectionJavaConversions;
import com.newrelic.agent.instrumentation.pointcuts.container.netty.DelegatingNettyHttpRequest;
import java.util.List;
import java.util.HashMap;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class Play2RequestPointCut extends PointCut implements EntryInvocationHandler
{
    private static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    static final String PLAY21CLASS = "play/api/mvc/Request$";
    static final String REQUEST_METHOD_NAME = "apply";
    static final String REQUEST_METHOD_DESC = "(Lplay/api/mvc/RequestHeader;Ljava/lang/Object;)Lplay/api/mvc/Request;";
    static final String PLAY20CLASS = "play/core/server/Server$class";
    static final String SERVER_METHOD_NAME = "invoke";
    static final String SERVER_METHOD_DESC = "(Lplay/core/server/Server;Lplay/api/mvc/Request;Lplay/api/mvc/Response;Lplay/api/mvc/Action;Lplay/api/Application;)V";
    
    public Play2RequestPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(Play2RequestPointCut.POINT_CUT_NAME, "play2_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return OrClassMatcher.getClassMatcher(new ExactClassMatcher("play/api/mvc/Request$"), new ExactClassMatcher("play/core/server/Server$class"));
    }
    
    private static MethodMatcher createMethodMatcher() {
        return OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("apply", "(Lplay/api/mvc/RequestHeader;Ljava/lang/Object;)Lplay/api/mvc/Request;"), new ExactMethodMatcher("invoke", "(Lplay/core/server/Server;Lplay/api/mvc/Request;Lplay/api/mvc/Response;Lplay/api/mvc/Action;Lplay/api/Application;)V"));
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Transaction tx = Transaction.getTransaction();
        if (!tx.isStarted()) {
            return;
        }
        final com.newrelic.api.agent.Request request = tx.getRootTransaction().getDispatcher().getRequest();
        final Map<String, List<Object>> extractedForm = new HashMap<String, List<Object>>();
        if (request instanceof DelegatingNettyHttpRequest) {
            final DelegatingNettyHttpRequest nettyRequest = (DelegatingNettyHttpRequest)request;
            Object bodyCandidate = args[1];
            if (args[1] instanceof Request) {
                final Request req = (Request)args[1];
                bodyCandidate = req.nr_body();
                final Object data = req.nr_queryString();
                if (data != null) {
                    final Map<?, ?> formValues = (Map<?, ?>)ScalaCollectionJavaConversions.asJavaMap(data);
                    for (final Map.Entry<?, ?> formValue : formValues.entrySet()) {
                        final List<?> values = (List<?>)ScalaCollectionJavaConversions.asJavaList(formValue.getValue());
                        extractedForm.put((String)formValue.getKey(), new ArrayList<Object>(values));
                    }
                }
            }
            else if (args[0] instanceof RequestHeader) {
                final RequestHeader rh = (RequestHeader)args[0];
                final Object data = rh.nr_queryString();
                if (data != null) {
                    final Map<?, ?> formValues = (Map<?, ?>)ScalaCollectionJavaConversions.asJavaMap(data);
                    for (final Map.Entry<?, ?> formValue : formValues.entrySet()) {
                        final List<?> values = (List<?>)ScalaCollectionJavaConversions.asJavaList(formValue.getValue());
                        extractedForm.put((String)formValue.getKey(), new ArrayList<Object>(values));
                    }
                }
            }
            if (bodyCandidate instanceof AnyContentAsFormUrlEncoded) {
                final AnyContentAsFormUrlEncoded formBody = (AnyContentAsFormUrlEncoded)bodyCandidate;
                final Object data = formBody.nr_data();
                if (data != null) {
                    final Map<?, ?> formValues = (Map<?, ?>)ScalaCollectionJavaConversions.asJavaMap(data);
                    for (final Map.Entry<?, ?> formValue : formValues.entrySet()) {
                        final List<?> values = (List<?>)ScalaCollectionJavaConversions.asJavaList(formValue.getValue());
                        if (extractedForm.containsKey(formValue.getKey())) {
                            extractedForm.get(formValue.getKey()).addAll(values);
                        }
                        else {
                            extractedForm.put((String)formValue.getKey(), new ArrayList<Object>(values));
                        }
                    }
                }
            }
            nettyRequest.setParameters(extractedForm);
        }
    }
    
    static {
        POINT_CUT_NAME = Play2RequestPointCut.class.getName();
    }
    
    @InterfaceMixin(originalClassName = { "play/api/mvc/AnyContentAsFormUrlEncoded" })
    public interface AnyContentAsFormUrlEncoded
    {
        public static final String CLASS = "play/api/mvc/AnyContentAsFormUrlEncoded";
        
        @FieldAccessor(fieldName = "data", existingField = true, fieldDesc = "Lscala/collection/immutable/Map;")
        Object nr_data();
    }
    
    @InterfaceMapper(originalInterfaceName = "play/api/mvc/RequestHeader", className = { "play/api/mvc/RequestHeader$$anon$4" })
    public interface RequestHeader
    {
        public static final String INTERFACE = "play/api/mvc/RequestHeader";
        public static final String CLASS = "play/api/mvc/RequestHeader$$anon$4";
        
        @MethodMapper(originalMethodName = "queryString", originalDescriptor = "()Lscala/collection/immutable/Map;")
        Object nr_queryString();
    }
    
    @InterfaceMapper(originalInterfaceName = "play/api/mvc/Request", className = { "play/core/server/netty/PlayDefaultUpstreamHandler$$anonfun$19$$anonfun$apply$21$$anon$1" })
    public interface Request
    {
        public static final String INTERFACE = "play/api/mvc/Request";
        public static final String CLASS = "play/core/server/netty/PlayDefaultUpstreamHandler$$anonfun$19$$anonfun$apply$21$$anon$1";
        
        @MethodMapper(originalMethodName = "body")
        Object nr_body();
        
        @MethodMapper(originalMethodName = "queryString", originalDescriptor = "()Lscala/collection/immutable/Map;")
        Object nr_queryString();
    }
}
