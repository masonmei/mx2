// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.cxf;

import java.text.MessageFormat;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import java.net.URISyntaxException;
import java.net.URI;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.util.Strings;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.lang.reflect.Method;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class CXFInvokerPointCut extends TracerFactoryPointCut
{
    private static final String CXF = "CXF";
    
    public CXFInvokerPointCut(final ClassTransformer classTransformer) {
        super(CXFInvokerPointCut.class, new ExactClassMatcher("org/apache/cxf/service/invoker/AbstractInvoker"), PointCut.createExactMethodMatcher("performInvocation", "(Lorg/apache/cxf/message/Exchange;Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object invoker, final Object[] args) {
        final Object service = args[1];
        final Method method = (Method)args[2];
        final String address = transaction.getInternalParameters().remove("cfx_end_point");
        if (address != null) {
            final StringBuilder path = new StringBuilder(address);
            if (!address.endsWith("/")) {
                path.append('/');
            }
            path.append(method.getName());
            this.setTransactionName(transaction, getCXFRequestUri(address, method));
        }
        else {
            Agent.LOG.log(Level.FINEST, "The CXF endpoint address is null.");
            this.setTransactionName(transaction, service.getClass().getName() + '/' + method.getName());
        }
        return new DefaultTracer(transaction, sig, invoker, new SimpleMetricNameFormat(Strings.join('/', "Java", service.getClass().getName(), method.getName())));
    }
    
    static String getCXFRequestUri(String address, final Method method) {
        try {
            address = new URI(address).getPath();
        }
        catch (URISyntaxException ex) {}
        final StringBuilder path = new StringBuilder();
        if (!address.startsWith("/")) {
            path.append('/');
        }
        path.append(address);
        if (!address.endsWith("/")) {
            path.append('/');
        }
        path.append(method.getName());
        return path.toString();
    }
    
    private void setTransactionName(final Transaction transaction, final String path) {
        if (!transaction.isTransactionNamingEnabled()) {
            return;
        }
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(transaction, TransactionNamePriority.FRAMEWORK)) {
            final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using CXF", path);
            Agent.LOG.finer(msg);
        }
        policy.setTransactionName(transaction, path, "CXF", TransactionNamePriority.FRAMEWORK);
    }
}
