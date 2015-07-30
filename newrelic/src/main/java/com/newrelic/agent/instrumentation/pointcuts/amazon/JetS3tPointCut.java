// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.amazon;

import com.newrelic.agent.tracers.ExternalComponentTracer;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class JetS3tPointCut extends AbstractJetS3tPointCut
{
    public JetS3tPointCut(final ClassTransformer classTransformer) {
        super(JetS3tPointCut.class, new MethodMatcher[] { new ExactMethodMatcher("listAllBuckets", "()[Lorg/jets3t/service/model/S3Bucket;") });
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object service, final Object[] args) {
        String host;
        String uri;
        try {
            host = this.getHost(service);
            uri = this.getUri(service);
        }
        catch (Exception e) {
            host = "storage";
            uri = "";
            final String msg = MessageFormat.format("Instrumentation error invoking {0} in {1}: {2}", sig, this.getClass().getName(), e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg, e);
            }
            else {
                Agent.LOG.log(Level.FINE, msg);
            }
        }
        return new ExternalComponentTracer(transaction, sig, service, host, "Jets3t", uri, new String[] { sig.getMethodName() });
    }
}
