// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import com.newrelic.agent.instrumentation.annotationmatchers.AnnotationMatcher;
import java.util.Collection;
import java.util.Set;

public interface ClassTransformerConfig extends Config
{
    boolean isCustomTracingEnabled();
    
    Set<String> getExcludes();
    
    Set<String> getIncludes();
    
    boolean computeFrames();
    
    long getShutdownDelayInNanos();
    
    boolean isEnabled();
    
    Collection<String> getJdbcStatements();
    
    AnnotationMatcher getIgnoreTransactionAnnotationMatcher();
    
    AnnotationMatcher getIgnoreApdexAnnotationMatcher();
    
    AnnotationMatcher getTraceAnnotationMatcher();
    
    boolean isGrantPackageAccess();
    
    Config getInstrumentationConfig(String p0);
}
