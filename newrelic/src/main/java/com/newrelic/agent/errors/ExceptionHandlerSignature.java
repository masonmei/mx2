// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.io.Writer;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.methodmatchers.InvalidMethodDescriptor;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class ExceptionHandlerSignature implements JSONStreamAware
{
    private final String className;
    private final String methodName;
    private final String methodDescription;
    
    public ExceptionHandlerSignature(final String className, final String methodName, final String methodDescription) throws InvalidMethodDescriptor {
        this.className = className;
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        new ExactMethodMatcher(methodName, methodDescription).validate();
    }
    
    public ExceptionHandlerSignature(final ClassMethodSignature sig) throws InvalidMethodDescriptor {
        this.className = sig.getClassName();
        this.methodName = sig.getMethodName();
        this.methodDescription = sig.getMethodDesc();
        new ExactMethodMatcher(this.methodName, this.methodDescription).validate();
    }
    
    private static Collection<String> getExceptionClassNames() {
        final List<Class<? extends Throwable>> classes = Arrays.asList(Throwable.class, Error.class, Exception.class);
        final Collection<String> classNames = new ArrayList<String>();
        for (final Class clazz : classes) {
            classNames.add(clazz.getName());
        }
        classNames.add("javax.servlet.ServletException");
        return classNames;
    }
    
    public int getExceptionArgumentIndex() {
        final Type[] types = Type.getArgumentTypes(this.methodDescription);
        final Collection<String> exceptionClassNames = getExceptionClassNames();
        for (int i = 0; i < types.length; ++i) {
            if (exceptionClassNames.contains(types[i].getClassName())) {
                return i;
            }
        }
        return -1;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public String getMethodDescription() {
        return this.methodDescription;
    }
    
    public ClassMatcher getClassMatcher() {
        return new ExactClassMatcher(this.className);
    }
    
    public MethodMatcher getMethodMatcher() {
        return new ExactMethodMatcher(this.methodName, this.methodDescription);
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        JSONArray.writeJSONString(Arrays.asList(this.className, this.methodName, this.methodDescription), out);
    }
    
    public String toString() {
        return this.className.replace('/', '.') + '.' + this.methodName + this.methodDescription;
    }
}
