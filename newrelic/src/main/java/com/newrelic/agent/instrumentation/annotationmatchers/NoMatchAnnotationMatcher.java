// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.annotationmatchers;

public class NoMatchAnnotationMatcher implements AnnotationMatcher
{
    public boolean matches(final String annotationDesc) {
        return false;
    }
}
