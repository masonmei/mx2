// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.amazon;

import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public abstract class AbstractJetS3tPointCut extends TracerFactoryPointCut
{
    protected static final String S3_BUCKET_CLASS = "Lorg/jets3t/service/model/S3Bucket;";
    protected static final String AMAZON_S3_SERVICE = "org.jets3t.service.impl.rest.httpclient.RestS3Service";
    protected static final String GOOGLE_STORAGE_SERVICE = "org.jets3t.service.impl.rest.httpclient.GoogleStorageService";
    protected static final String AMAZON_HOST = "amazon";
    protected static final String GOOGLE_HOST = "google";
    protected static final String DEFAULT_STORAGE = "storage";
    
    protected AbstractJetS3tPointCut(final Class<? extends TracerFactoryPointCut> tracerFactory, final MethodMatcher... methodMatchers) {
        super(tracerFactory, new ExactClassMatcher("org/jets3t/service/S3Service"), OrMethodMatcher.getMethodMatcher(methodMatchers));
    }
    
    protected String getHost(final Object service) {
        if (service != null) {
            final String serviceImplName = service.getClass().getCanonicalName();
            if ("org.jets3t.service.impl.rest.httpclient.RestS3Service".equals(serviceImplName)) {
                return "amazon";
            }
            if ("org.jets3t.service.impl.rest.httpclient.GoogleStorageService".equals(serviceImplName)) {
                return "google";
            }
        }
        return "storage";
    }
    
    protected String getUri(final Object service) {
        if (service != null && service instanceof StorageService) {
            final StorageService storageService = (StorageService)service;
            return storageService.getEndpoint();
        }
        return "";
    }
}
