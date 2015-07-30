// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.net;

import com.newrelic.agent.tracers.IOTracer;
import com.newrelic.agent.tracers.ExternalComponentTracer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.newrelic.agent.instrumentation.InstrumentUtils;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.net.URL;
import java.net.HttpURLConnection;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class HttpInputStreamPointCut extends TracerFactoryPointCut
{
    public static final String HTTP_INPUT_STREAM_CLASS_NAME = "sun/net/www/protocol/http/HttpURLConnection$HttpInputStream";
    
    public HttpInputStreamPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("http_input_stream", null, false), new ExactClassMatcher("sun/net/www/protocol/http/HttpURLConnection$HttpInputStream"), PointCut.createExactMethodMatcher("read", "([BII)I"));
        classTransformer.getClassNameFilter().addIncludeClass("sun/net/www/protocol/http/HttpURLConnection$HttpInputStream");
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object inputStream, final Object[] args) {
        URL url = transaction.getTransactionCache().getURL(inputStream);
        if (url == null) {
            try {
                final Method getUrlMethod = HttpURLConnection.class.getMethod("getURL", (Class<?>[])new Class[0]);
                final Field this$0 = inputStream.getClass().getDeclaredField("this$0");
                this$0.setAccessible(true);
                final HttpURLConnection connection = (HttpURLConnection)this$0.get(inputStream);
                url = (URL)getUrlMethod.invoke(connection, new Object[0]);
                transaction.getTransactionCache().putURL(inputStream, url);
            }
            catch (Throwable t) {
                Agent.LOG.log(Level.FINER, "Error getting url from http input stream", t);
            }
        }
        String host;
        String uri;
        if (url != null) {
            host = url.getHost();
            uri = InstrumentUtils.getURI(url);
        }
        else {
            host = "unknown";
            uri = "";
        }
        String operation;
        if (sig != null) {
            operation = sig.getMethodName();
        }
        else {
            operation = "";
        }
        return new HttpInputStreamTracer((PointCut)this, transaction, sig, inputStream, host, "HttpInputStream", uri, new String[] { operation });
    }
    
    private static final class HttpInputStreamTracer extends ExternalComponentTracer implements IOTracer
    {
        private HttpInputStreamTracer(final PointCut pc, final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final String[] operations) {
            super(transaction, sig, object, host, library, uri, operations);
        }
    }
}
