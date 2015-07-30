// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.annotationmatchers;

public class ClassNameAnnotationMatcher implements AnnotationMatcher
{
    private final String simpleClassName;
    private final boolean fullMatch;
    
    public ClassNameAnnotationMatcher(final String className) {
        this(className, true);
    }
    
    public ClassNameAnnotationMatcher(String className, final boolean fullMatch) {
        if (!fullMatch && !className.endsWith(";")) {
            className += ";";
        }
        this.simpleClassName = className;
        this.fullMatch = fullMatch;
    }
    
    public boolean matches(final String annotationDesc) {
        if (this.fullMatch) {
            return annotationDesc.equals(this.simpleClassName);
        }
        return annotationDesc.endsWith(this.simpleClassName);
    }
}
