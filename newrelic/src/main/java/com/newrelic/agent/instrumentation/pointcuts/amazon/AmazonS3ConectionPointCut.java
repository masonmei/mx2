// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.amazon;

import com.newrelic.agent.tracers.ExternalComponentTracer;
import com.newrelic.agent.tracers.AbstractTracerFactory;
import java.util.Collections;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import java.util.Iterator;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import java.util.HashMap;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.TracerFactory;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import java.util.Map;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class AmazonS3ConectionPointCut extends TracerFactoryPointCut
{
    private static final Map<MethodMatcher, TracerFactory> methodMatcherTracers;
    
    public AmazonS3ConectionPointCut(final ClassTransformer classTransformer) {
        super(AmazonS3ConectionPointCut.class, new ExactClassMatcher("com/amazon/s3/AWSAuthConnection"), OrMethodMatcher.getMethodMatcher((MethodMatcher[])AmazonS3ConectionPointCut.methodMatcherTracers.keySet().toArray(new MethodMatcher[0])));
    }
    
    private static Map<MethodMatcher, TracerFactory> createTracerFactories() {
        final Map<MethodMatcher, TracerFactory> factories = new HashMap<MethodMatcher, TracerFactory>() {
            {
                addBasicTracerFactory(this, "listAllMyBuckets", "(Ljava/util/Map;)Lcom/amazon/s3/ListAllMyBucketsResponse;");
                addBucketTracerFactory(this, "createBucket", "(Ljava/lang/String;Ljava/util/Map;)Lcom/amazon/s3/Response;");
                addBucketTracerFactory(this, "createBucket", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;)Lcom/amazon/s3/Response;");
                addBucketTracerFactory(this, "listBucket", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/util/Map;)Lcom/amazon/s3/ListBucketResponse;");
                addBucketTracerFactory(this, "listBucket", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/util/Map;)Lcom/amazon/s3/ListBucketResponse;");
                addBucketTracerFactory(this, "deleteBucket", "(Ljava/lang/String;Ljava/util/Map;)Lcom/amazon/s3/Response;");
                addBucketTracerFactory(this, "getBucketLocation", "(Ljava/lang/String;)Lcom/amazon/s3/LocationResponse;");
                addBucketTracerFactory(this, "get", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;)Lcom/amazon/s3/GetResponse;");
                addBucketTracerFactory(this, "put", "(Ljava/lang/String;Ljava/lang/String;Lcom/amazon/s3/S3Object;Ljava/util/Map;)Lcom/amazon/s3/Response;");
                addBucketTracerFactory(this, "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;)Lcom/amazon/s3/Response;");
                addBucketTracerFactory(this, "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;Ljava/util/Map;)Lcom/amazon/s3/Response;");
                addBucketTracerFactory(this, "delete", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;)Lcom/amazon/s3/Response;");
            }
        };
        return factories;
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        for (final Map.Entry<MethodMatcher, TracerFactory> entry : AmazonS3ConectionPointCut.methodMatcherTracers.entrySet()) {
            if (entry.getKey().matches(-1, sig.getMethodName(), sig.getMethodDesc(), MethodMatcher.UNSPECIFIED_ANNOTATIONS)) {
                return entry.getValue().getTracer(transaction, sig, object, args);
            }
        }
        return null;
    }
    
    private static void addBasicTracerFactory(final Map<MethodMatcher, TracerFactory> map, String methodName, String methodDesc) {
        methodName = methodName.intern();
        methodDesc = methodDesc.intern();
        map.put(new ExactMethodMatcher(methodName, methodDesc), new BasicTracerFactory(methodName));
    }
    
    private static void addBucketTracerFactory(final Map<MethodMatcher, TracerFactory> map, String methodName, String methodDesc) {
        methodName = methodName.intern();
        methodDesc = methodDesc.intern();
        map.put(new ExactMethodMatcher(methodName, methodDesc), new BucketTracerFactory(methodName));
    }
    
    static {
        methodMatcherTracers = Collections.unmodifiableMap((Map<? extends MethodMatcher, ? extends TracerFactory>)createTracerFactories());
    }
    
    private static class BasicTracerFactory extends AbstractTracerFactory
    {
        private final String operation;
        
        public BasicTracerFactory(final String operation) {
            this.operation = operation;
        }
        
        public String getOperation() {
            return this.operation;
        }
        
        public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
            final String host = "amazon";
            final String uri = "";
            return new ExternalComponentTracer(transaction, sig, object, host, "S3", uri, new String[] { this.getOperation() });
        }
    }
    
    private static class BucketTracerFactory extends BasicTracerFactory
    {
        public BucketTracerFactory(final String operation) {
            super(operation);
        }
        
        public Tracer getTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
            final String host = "amazon";
            final String uri = "";
            return new ExternalComponentTracer(transaction, sig, object, host, "S3", uri, new String[] { this.getOperation(), (String)args[0] });
        }
    }
}
