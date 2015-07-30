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
public class JetS3tBucketPointCut extends AbstractJetS3tPointCut
{
    public JetS3tBucketPointCut(final ClassTransformer classTransformer) {
        super(JetS3tBucketPointCut.class, new MethodMatcher[] { new ExactMethodMatcher("listObjects", new String[] { "(Lorg/jets3t/service/model/S3Bucket;)[Lorg/jets3t/service/model/S3Object;", "(Lorg/jets3t/service/model/S3Bucket;Ljava/lang/String;Ljava/lang/String;)[Lorg/jets3t/service/model/S3Object;", "(Lorg/jets3t/service/model/S3Bucket;Ljava/lang/String;Ljava/lang/String;J)[Lorg/jets3t/service/model/S3Object;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)[Lorg/jets3t/service/model/S3Object;" }), new ExactMethodMatcher("createBucket", new String[] { "(Ljava/lang/String;Ljava/lang/String;)Lorg/jets3t/service/model/S3Bucket;", "(Ljava/lang/String;)Lorg/jets3t/service/model/S3Bucket;", "(Lorg/jets3t/service/model/S3Bucket;)Lorg/jets3t/service/model/S3Bucket;" }), new ExactMethodMatcher("deleteBucket", new String[] { "(Lorg/jets3t/service/model/S3Bucket;)V", "(Ljava/lang/String;)V" }), new ExactMethodMatcher("deleteObject", new String[] { "(Lorg/jets3t/service/model/S3Bucket;Ljava/lang/String;)V", "(Ljava/lang/String;Ljava/lang/String;)V" }), new ExactMethodMatcher("getObject", new String[] { "(Lorg/jets3t/service/model/S3Bucket;Ljava/lang/String;)Lorg/jets3t/service/model/S3Object;", "(Lorg/jets3t/service/model/S3Bucket;Ljava/lang/String;Ljava/util/Calendar;Ljava/util/Calendar;[Ljava/lang/String;[Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Long;)Lorg/jets3t/service/model/S3Object;", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Calendar;Ljava/util/Calendar;[Ljava/lang/String;[Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Long;)Lorg/jets3t/service/model/S3Object;" }), new ExactMethodMatcher("putObject", new String[] { "(Ljava/lang/String;Lorg/jets3t/service/model/S3Object;)Lorg/jets3t/service/model/S3Object;", "(Lorg/jets3t/service/model/S3Bucket;Lorg/jets3t/service/model/S3Object;)Lorg/jets3t/service/model/S3Object;" }), new ExactMethodMatcher("copyObject", new String[] { "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/jets3t/service/model/S3Object;ZLjava/util/Calendar;Ljava/util/Calendar;[Ljava/lang/String;[Ljava/lang/String;)Ljava/util/Map;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/jets3t/service/model/S3Object;Z)Ljava/util/Map;" }) });
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object service, final Object[] args) {
        String host = null;
        String uri;
        try {
            host = this.getHost(service);
            uri = this.getUri(service);
        }
        catch (Exception e) {
            if (host == null) {
                host = "storage";
            }
            uri = "";
            final String msg = MessageFormat.format("Instrumentation error invoking {0} in {1}: {2}", sig, this.getClass().getName(), e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg, e);
            }
            else {
                Agent.LOG.log(Level.FINE, msg);
            }
        }
        return new ExternalComponentTracer(transaction, sig, service, host, "Jets3t", uri, new String[] { sig.getMethodName(), args[0].toString() });
    }
}
