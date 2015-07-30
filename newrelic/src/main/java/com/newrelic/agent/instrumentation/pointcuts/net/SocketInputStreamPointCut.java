// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.net;

import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.IOTracer;
import com.newrelic.agent.tracers.ExternalComponentTracer;
import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.service.ServiceFactory;
import java.lang.reflect.Field;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.MetricNameFormatWithHost;
import java.net.Socket;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.tracers.ExternalComponentPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SocketInputStreamPointCut extends ExternalComponentPointCut
{
    public static final String[] INPUT_STREAM_METHODS;
    public static final String SOCKET_INPUT_STREAM_CLASS_NAME = "java/net/SocketInputStream";
    
    public SocketInputStreamPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("socket_input_stream", null, false), new ExactClassMatcher("java/net/SocketInputStream"), PointCut.createExactMethodMatcher("read", SocketInputStreamPointCut.INPUT_STREAM_METHODS));
        classTransformer.getClassNameFilter().addIncludeClass("java/net/SocketInputStream");
    }
    
    protected Tracer getExternalTracer(final Transaction transaction, final ClassMethodSignature sig, final Object inputStream, final Object[] args) {
        MetricNameFormatWithHost metricFormat = transaction.getTransactionCache().getMetricNameFormatWithHost(inputStream);
        if (metricFormat == null) {
            try {
                final Field socketField = inputStream.getClass().getDeclaredField("socket");
                socketField.setAccessible(true);
                final Socket socket = (Socket)socketField.get(inputStream);
                final String host = socket.getInetAddress().getHostName();
                metricFormat = MetricNameFormatWithHost.create(host, "SocketInputStream");
                transaction.getTransactionCache().putMetricNameFormatWithHost(inputStream, metricFormat);
            }
            catch (Throwable t) {
                metricFormat = MetricNameFormatWithHost.create("errorFetchingHost", "SocketInputStream");
                transaction.getTransactionCache().putMetricNameFormatWithHost(inputStream, metricFormat);
                Agent.LOG.log(Level.FINER, "Error getting url from http input stream", t);
            }
        }
        return new SocketInputStreamTracer((PointCut)this, transaction, sig, inputStream, metricFormat);
    }
    
    public void noticeTransformerStarted(final ClassTransformer classTransformer) {
        this.avoidUnsatisfiedLinkError();
        final InstrumentationProxy instrumentation = ServiceFactory.getAgent().getInstrumentation();
        try {
            instrumentation.retransformUninstrumentedClasses("java.net.SocketInputStream");
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINER, "Unable to retransform SocketInputStream", e);
        }
    }
    
    private void avoidUnsatisfiedLinkError() {
        Socket sock = null;
        try {
            sock = new Socket();
            sock.getInputStream();
        }
        catch (Exception e) {
            if (sock != null) {
                try {
                    sock.close();
                }
                catch (Exception ex) {}
            }
        }
    }
    
    static {
        INPUT_STREAM_METHODS = new String[] { "()I", "([BII)I" };
    }
    
    private static final class SocketInputStreamTracer extends ExternalComponentTracer implements IOTracer
    {
        private SocketInputStreamTracer(final PointCut pc, final Transaction transaction, final ClassMethodSignature sig, final Object object, final MetricNameFormatWithHost metricNameFormat) {
            super(transaction, sig, object, metricNameFormat.getHost(), metricNameFormat);
        }
        
        public TransactionSegment getTransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final long startTime, final TransactionSegment lastSibling) {
            if (lastSibling != null && lastSibling.getMetricName().equals(this.getTransactionSegmentName())) {
                lastSibling.merge(this);
                return lastSibling;
            }
            return super.getTransactionSegment(ttConfig, sqlObfuscator, startTime, lastSibling);
        }
    }
}
