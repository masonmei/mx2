// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.netty;

import com.newrelic.agent.instrumentation.pointcuts.MethodMapper;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.servlet.BasicRequestRootTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.TransactionState;
import com.newrelic.agent.async.AsyncTransactionState;
import java.text.MessageFormat;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.ClassTransformer;
import java.util.concurrent.atomic.AtomicBoolean;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class NettyDispatcherPointCut extends TracerFactoryPointCut
{
    public static final String INSTRUMENTATION_GROUP_NAME = "netty_instrumentation";
    private static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    static final String CLASS = "org/jboss/netty/handler/codec/frame/FrameDecoder";
    static final String HTTP_CLASS = "org/jboss/netty/handler/codec/http/HttpRequestDecoder";
    static final String METHOD_NAME = "unfoldAndFireMessageReceived";
    static final String METHOD_DESC = "(Lorg/jboss/netty/channel/ChannelHandlerContext;Ljava/net/SocketAddress;Ljava/lang/Object;)V";
    static final String NETTY_DISPATCHER = "NettyDispatcher";
    private final AtomicBoolean firstRun;
    private final String alternateDispatchClassName;
    
    public NettyDispatcherPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
        this.firstRun = new AtomicBoolean(true);
        this.alternateDispatchClassName = (String)ServiceFactory.getConfigService().getDefaultAgentConfig().getValue("class_transformer.netty_dispatcher_class");
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(NettyDispatcherPointCut.POINT_CUT_NAME, "netty_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("org/jboss/netty/handler/codec/frame/FrameDecoder");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("unfoldAndFireMessageReceived", "(Lorg/jboss/netty/channel/ChannelHandlerContext;Ljava/net/SocketAddress;Ljava/lang/Object;)V");
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    public final Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        boolean isExpectedType = object instanceof HttpRequestDecoder;
        final boolean isRequestPresent = args[2] instanceof NettyHttpRequest;
        if (this.alternateDispatchClassName != null && isRequestPresent && !isExpectedType) {
            isExpectedType = object.getClass().getName().equals(this.alternateDispatchClassName);
        }
        if (!isExpectedType || !isRequestPresent) {
            Agent.LOG.log(Level.FINEST, "NettyDispatcher: Skipping message {1} recieved for {0}", new Object[] { object.getClass(), args[2] });
            Transaction.clearTransaction();
            return null;
        }
        return this.buildTracer(tx, sig, object, args);
    }
    
    Tracer buildTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (this.firstRun.get()) {
            Agent.LOG.fine("Clearing first transaction to allow system to initalize.");
            this.firstRun.set(false);
            Transaction.clearTransaction();
            return null;
        }
        final Tracer rootTracer = tx.getRootTracer();
        if (rootTracer != null) {
            Agent.LOG.log(Level.FINER, "NettyDispatcher: rootTracer not null. Already in a transaction? {0}->{1}", new Object[] { tx, rootTracer });
            return null;
        }
        final Request httpRequest = DelegatingNettyHttpRequest.create((NettyHttpRequest)args[2]);
        final Response httpResponse = DelegatingNettyHttpResponse.create(null);
        final Tracer tracer = this.createTracer(tx, sig, object, httpRequest, httpResponse);
        if (tracer != null) {
            this.setTransactionName(tx);
        }
        if (args[0] instanceof ChannelHandlerContext) {
            final ChannelHandlerContext ctx = (ChannelHandlerContext)args[0];
            if (ctx._nr_getChannel() instanceof TransactionHolder) {
                final TransactionHolder th = (TransactionHolder)ctx._nr_getChannel();
                Agent.LOG.log(Level.FINER, "Setting {0} on holder {1}", new Object[] { tx, th });
                th._nr_setTransaction(tx);
                tx.getTransactionState().asyncJobStarted(th);
            }
            else {
                Agent.LOG.log(Level.FINER, "Unable to get holder from {1}", new Object[] { ctx });
            }
        }
        else {
            Agent.LOG.log(Level.FINER, "Invalid context {1}", new Object[] { args[0] });
        }
        return tracer;
    }
    
    private void setTransactionName(final Transaction tx) {
        if (!tx.isTransactionNamingEnabled()) {
            return;
        }
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(tx, TransactionNamePriority.SERVLET_NAME)) {
            final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Netty Http Decoder", "NettyDispatcher");
            Agent.LOG.finer(msg);
        }
        policy.setTransactionName(tx, "NettyDispatcher", "NettyDispatcher", TransactionNamePriority.SERVLET_NAME);
    }
    
    private Tracer createTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Request httpRequest, final Response httpResponse) {
        final TransactionState transactionState = tx.getTransactionState();
        if (!(transactionState instanceof AsyncTransactionState)) {
            tx.setTransactionState(new AsyncTransactionState(tx.getTransactionActivity()));
        }
        try {
            return new BasicRequestRootTracer(tx, sig, object, httpRequest, httpResponse, new SimpleMetricNameFormat("Java/org.jboss.netty.handler.codec.http.HttpRequestDecoder/unfoldAndFireMessageReceived"));
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
    
    static {
        POINT_CUT_NAME = NettyDispatcherPointCut.class.getName();
    }
    
    @InterfaceMixin(originalClassName = { "org/jboss/netty/handler/codec/http/HttpRequestDecoder" })
    public interface HttpRequestDecoder
    {
    }
    
    @InterfaceMapper(className = { "org/jboss/netty/channel/DefaultChannelPipeline$DefaultChannelHandlerContext" }, originalInterfaceName = "org/jboss/netty/channel/ChannelHandlerContext")
    public interface ChannelHandlerContext
    {
        public static final String CLASS = "org/jboss/netty/channel/DefaultChannelPipeline$DefaultChannelHandlerContext";
        public static final String INTERFACE = "org/jboss/netty/channel/ChannelHandlerContext";
        
        @MethodMapper(originalMethodName = "getChannel", originalDescriptor = "()Lorg/jboss/netty/channel/Channel;", invokeInterface = false)
        Object _nr_getChannel();
    }
}
