// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.Agent;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

public class AnnotationMethodMatcher implements MethodMatcher
{
    private final Type annotationType;
    private final String annotationDesc;
    
    public AnnotationMethodMatcher(final Type annotationType) {
        this.annotationType = annotationType;
        this.annotationDesc = annotationType.getDescriptor();
    }
    
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        if (annotations == MethodMatcher.UNSPECIFIED_ANNOTATIONS) {
            Agent.LOG.finer("The annotation method matcher will not work if annotations aren't specified");
        }
        return annotations.contains(this.annotationDesc);
    }
    
    public Method[] getExactMethods() {
        return null;
    }
    
    public Type getAnnotationType() {
        return this.annotationType;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.annotationDesc == null) ? 0 : this.annotationDesc.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final AnnotationMethodMatcher other = (AnnotationMethodMatcher)obj;
        if (this.annotationType == null) {
            if (other.annotationType != null) {
                return false;
            }
        }
        else if (!this.annotationType.equals(other.annotationType)) {
            return false;
        }
        return true;
    }
}
