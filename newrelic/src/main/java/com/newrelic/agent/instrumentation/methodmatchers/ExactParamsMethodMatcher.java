// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;

public class ExactParamsMethodMatcher implements MethodMatcher
{
    private final String name;
    private final String parameterDescriptor;
    
    private ExactParamsMethodMatcher(final String pName, final String paramDescriptorWithParenthesis) {
        this.name = pName;
        this.parameterDescriptor = paramDescriptorWithParenthesis;
    }
    
    public static ExactParamsMethodMatcher createExactParamsMethodMatcher(final String methodName, final String inputDescriptor, final String className) throws RuntimeException {
        if (methodName == null) {
            throw new RuntimeException("Method name can not be null or empty.");
        }
        final String methodNameTrimmed = methodName.trim();
        if (methodNameTrimmed.length() == 0) {
            throw new RuntimeException("Method name can not be null or empty.");
        }
        if (inputDescriptor == null) {
            throw new RuntimeException("Parameter descriptor can not be null or empty.");
        }
        final String inputDescriptorTrimmed = inputDescriptor.trim();
        if (inputDescriptorTrimmed.length() == 0) {
            throw new RuntimeException("Parameter descriptor can not be null or empty.");
        }
        return new ExactParamsMethodMatcher(methodNameTrimmed, inputDescriptorTrimmed);
    }
    
    public boolean matches(final int access, final String pName, final String pDesc, final Set<String> annotations) {
        return this.name.equals(pName) && pDesc != null && pDesc.startsWith(this.parameterDescriptor);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = 31 * result + ((this.parameterDescriptor == null) ? 0 : this.parameterDescriptor.hashCode());
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
        final ExactParamsMethodMatcher other = (ExactParamsMethodMatcher)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.parameterDescriptor == null) {
            if (other.parameterDescriptor != null) {
                return false;
            }
        }
        else if (!this.parameterDescriptor.equals(other.parameterDescriptor)) {
            return false;
        }
        return true;
    }
    
    public String toString() {
        return "ExactParamsMethodMatcher(" + this.name + ", " + this.parameterDescriptor + ")";
    }
    
    public Method[] getExactMethods() {
        return null;
    }
}
