// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import com.newrelic.agent.tracers.IOTracer;
import com.newrelic.agent.tracers.ExternalComponentTracer;
import java.net.MalformedURLException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.InstrumentUtils;
import java.net.URL;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class XmlRpcPointCut extends TracerFactoryPointCut
{
    public XmlRpcPointCut(final ClassTransformer classTransformer) {
        super(XmlRpcPointCut.class, new InterfaceMatcher("javax/xml/rpc/Call"), PointCut.createExactMethodMatcher("invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", "(Ljavax/xml/namespace/QName;[Ljava/lang/Object;)Ljava/lang/Object;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object call, final Object[] args) {
        try {
            final String endPoint = (String)call.getClass().getMethod("getTargetEndpointAddress", (Class<?>[])new Class[0]).invoke(call, new Object[0]);
            try {
                final URL url = new URL(endPoint);
                final String uri = InstrumentUtils.getURI(url);
                String methodName;
                if (sig == null) {
                    methodName = "";
                }
                else {
                    methodName = sig.getMethodName();
                }
                return new XmlRpcTracer((PointCut)this, transaction, sig, call, url.getHost(), "XmlRpc", uri, new String[] { methodName });
            }
            catch (MalformedURLException e) {
                Agent.LOG.log(Level.FINE, "Unable to parse the target endpoint address for an XML RPC call", e);
            }
        }
        catch (Throwable e2) {
            Agent.LOG.log(Level.FINE, "Unable to get the target endpoint address for an XML RPC call", e2);
        }
        return null;
    }
    
    private static final class XmlRpcTracer extends ExternalComponentTracer implements IOTracer
    {
        private XmlRpcTracer(final PointCut pc, final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final String[] operations) {
            super(transaction, sig, object, host, library, uri, operations);
        }
    }
}
