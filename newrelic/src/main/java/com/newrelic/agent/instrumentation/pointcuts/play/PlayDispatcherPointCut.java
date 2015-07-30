// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.api.agent.Request;
import com.newrelic.api.agent.Response;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.tracers.servlet.BasicRequestRootTracer;
import com.newrelic.agent.TransactionState;
import com.newrelic.agent.tracers.RetryException;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class PlayDispatcherPointCut extends TracerFactoryPointCut
{
    public static final String PLAY_INSTRUMENTATION_GROUP_NAME = "play_instrumentation";
    public static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    private static final String ACTION_INVOKER_CLASS = "play/mvc/ActionInvoker";
    private static final String SCOPE_PARAMS_CLASS = "play/mvc/Scope$Params";
    private static final String HTTP_COOKIE_CLASS = "play/mvc/Http$Cookie";
    private static final String HTTP_HEADER_CLASS = "play/mvc/Http$Header";
    private static final String HTTP_REQUEST_CLASS = "play/mvc/Http$Request";
    private static final String HTTP_RESPONSE_CLASS = "play/mvc/Http$Response";
    private static final String INVOKE_METHOD_NAME = "invoke";
    private static final String INVOKE_METHOD_DESC = "(Lplay/mvc/Http$Request;Lplay/mvc/Http$Response;)V";
    public static final String UNKNOWN_CONTROLLER_ACTION = "UNKNOWN";
    public static final String PLAY_CONTROLLER_ACTION = "PlayControllerAction";
    
    public PlayDispatcherPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(PlayDispatcherPointCut.POINT_CUT_NAME, "play_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("play/mvc/ActionInvoker");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("invoke", "(Lplay/mvc/Http$Request;Lplay/mvc/Http$Response;)V");
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    public final Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Tracer rootTracer = tx.getRootTracer();
        if (rootTracer != null) {
            return null;
        }
        final PlayHttpRequest request = (PlayHttpRequest)args[0];
        final Transaction savedTx = this.getAndClearSavedTransaction(request);
        if (savedTx != null) {
            this.resumeTransaction(savedTx);
            throw new RetryException();
        }
        TransactionState transactionState = tx.getTransactionState();
        if (!(transactionState instanceof PlayTransactionStateImpl)) {
            transactionState = new PlayTransactionStateImpl(request);
            tx.setTransactionState(transactionState);
            throw new RetryException();
        }
        final Tracer tracer = this.createTracer(tx, sig, object, args);
        if (tracer != null) {
            this.setTransactionName(tx, request);
        }
        return tracer;
    }
    
    private Transaction getAndClearSavedTransaction(final PlayHttpRequest request) {
        final Transaction savedTx = (Transaction)request._nr_getTransaction();
        if (savedTx == null) {
            return null;
        }
        request._nr_setTransaction(null);
        return savedTx;
    }
    
    private void resumeTransaction(final Transaction savedTx) {
        final TransactionState transactionState = savedTx.getTransactionState();
        transactionState.resume();
        Transaction.clearTransaction();
        Transaction.setTransaction(savedTx);
    }
    
    private Tracer createTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        try {
            return new BasicRequestRootTracer(tx, sig, object, this.getRequest(tx, sig, object, args), this.getResponse(tx, sig, object, args), this.getMetricNameFormat(tx, sig, object, args));
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unable to create request dispatcher tracer: {0}", e);
            if (Agent.LOG.isFinestEnabled()) {
                Agent.LOG.log(Level.WARNING, msg, e);
            }
            else {
                Agent.LOG.warning(msg);
            }
            return null;
        }
    }
    
    private MetricNameFormat getMetricNameFormat(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        return new SimpleMetricNameFormat("RequestDispatcher", ClassMethodMetricNameFormat.getMetricName(sig, object, "RequestDispatcher"));
    }
    
    private Response getResponse(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) throws Exception {
        return DelegatingPlayHttpResponse.create((PlayHttpResponse)args[1]);
    }
    
    private Request getRequest(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) throws Exception {
        return DelegatingPlayHttpRequest.create((PlayHttpRequest)args[0]);
    }
    
    private void setTransactionName(final Transaction tx, final PlayHttpRequest request) {
        if (!tx.isTransactionNamingEnabled()) {
            return;
        }
        String action = request._nr_getAction();
        action = ((action == null) ? "UNKNOWN" : action);
        this.setTransactionName(tx, action);
    }
    
    private void setTransactionName(final Transaction tx, final String action) {
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(tx, TransactionNamePriority.FRAMEWORK_LOW)) {
            final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Play controller action", action);
            Agent.LOG.finer(msg);
        }
        policy.setTransactionName(tx, action, "PlayControllerAction", TransactionNamePriority.FRAMEWORK_LOW);
    }
    
    static {
        POINT_CUT_NAME = PlayDispatcherPointCut.class.getName();
    }
    
    private static class DelegatingPlayHttpRequest implements Request
    {
        private final PlayHttpRequest delegate;
        
        private DelegatingPlayHttpRequest(final PlayHttpRequest delegate) {
            this.delegate = delegate;
        }
        
        public HeaderType getHeaderType() {
            return HeaderType.HTTP;
        }
        
        public Enumeration<?> getParameterNames() {
            final PlayScopeParams playScopeParams = this.getScopeParams();
            if (playScopeParams == null) {
                return null;
            }
            final Map<String, String[]> params = playScopeParams.all();
            return Collections.enumeration((Collection<?>)params.keySet());
        }
        
        public String[] getParameterValues(final String name) {
            final PlayScopeParams playScopeParams = this.getScopeParams();
            if (playScopeParams == null) {
                return new String[0];
            }
            return playScopeParams.getAll(name);
        }
        
        public Object getAttribute(final String name) {
            return null;
        }
        
        public String getRequestURI() {
            return this.delegate._nr_getUrl();
        }
        
        public String getHeader(final String name) {
            if (name == null) {
                return null;
            }
            final Map<?, ?> headers = this.delegate._nr_getHeaders();
            PlayHttpHeader header = (PlayHttpHeader)headers.get(name);
            if (header != null) {
                return header.value();
            }
            header = (PlayHttpHeader)headers.get(name.toLowerCase());
            return (header == null) ? null : header.value();
        }
        
        public String getRemoteUser() {
            return null;
        }
        
        public String getCookieValue(final String name) {
            if (name == null) {
                return null;
            }
            final Map<?, ?> cookies = this.delegate._nr_getCookies();
            final PlayHttpCookie cookie = (PlayHttpCookie)cookies.get(name);
            return (cookie == null) ? null : cookie._nr_getValue();
        }
        
        private PlayScopeParams getScopeParams() {
            final Object scopeParams = this.delegate._nr_getParams();
            if (scopeParams instanceof PlayScopeParams) {
                return (PlayScopeParams)scopeParams;
            }
            return null;
        }
        
        static Request create(final PlayHttpRequest delegate) {
            return (Request)new DelegatingPlayHttpRequest(delegate);
        }
    }
    
    private static class DelegatingPlayHttpResponse implements Response
    {
        private final PlayHttpResponse delegate;
        
        private DelegatingPlayHttpResponse(final PlayHttpResponse delegate) {
            this.delegate = delegate;
        }
        
        public HeaderType getHeaderType() {
            return HeaderType.HTTP;
        }
        
        public String getStatusMessage() {
            return null;
        }
        
        public void setHeader(final String name, final String value) {
            this.delegate.setHeader(name, value);
        }
        
        public int getStatus() {
            final Integer status = this.delegate._nr_getResponseStatus();
            return (status == null) ? 0 : status;
        }
        
        public String getContentType() {
            return this.delegate._nr_getContentType();
        }
        
        static Response create(final PlayHttpResponse delegate) {
            return (Response)new DelegatingPlayHttpResponse(delegate);
        }
    }
    
    @InterfaceMixin(originalClassName = { "play/mvc/Http$Response" })
    public interface PlayHttpResponse
    {
        void setHeader(String p0, String p1);
        
        @FieldAccessor(fieldName = "status", existingField = true)
        Integer _nr_getResponseStatus();
        
        @FieldAccessor(fieldName = "contentType", existingField = true)
        String _nr_getContentType();
    }
    
    @InterfaceMixin(originalClassName = { "play/mvc/Http$Request" })
    public interface PlayHttpRequest
    {
        @FieldAccessor(fieldName = "transaction")
        void _nr_setTransaction(Object p0);
        
        @FieldAccessor(fieldName = "transaction")
        Object _nr_getTransaction();
        
        @FieldAccessor(fieldName = "headers", existingField = true)
        Map<?, ?> _nr_getHeaders();
        
        @FieldAccessor(fieldName = "cookies", existingField = true)
        Map<?, ?> _nr_getCookies();
        
        @FieldAccessor(fieldName = "url", existingField = true)
        String _nr_getUrl();
        
        @FieldAccessor(fieldName = "action", existingField = true)
        String _nr_getAction();
        
        @FieldAccessor(fieldName = "params", fieldDesc = "Lplay/mvc/Scope$Params;", existingField = true)
        Object _nr_getParams();
    }
    
    @InterfaceMixin(originalClassName = { "play/mvc/Http$Cookie" })
    public interface PlayHttpCookie
    {
        @FieldAccessor(fieldName = "value", existingField = true)
        String _nr_getValue();
    }
    
    @InterfaceMixin(originalClassName = { "play/mvc/Scope$Params" })
    public interface PlayScopeParams
    {
        String[] getAll(String p0);
        
        Map<String, String[]> all();
    }
    
    @InterfaceMixin(originalClassName = { "play/mvc/Http$Header" })
    public interface PlayHttpHeader
    {
        String value();
    }
}
