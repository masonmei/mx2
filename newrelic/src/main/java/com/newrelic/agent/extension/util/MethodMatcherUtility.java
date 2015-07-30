// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.util;

import java.text.MessageFormat;
import com.newrelic.agent.instrumentation.methodmatchers.ExactParamsMethodMatcher;
import com.newrelic.agent.extension.beans.MethodParameters;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactReturnTypeMethodMatcher;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.util.asm.Utils;
import java.util.Iterator;
import java.util.Collection;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.Agent;
import java.util.LinkedList;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import java.util.Map;
import com.newrelic.agent.extension.beans.Extension;
import java.util.List;

public class MethodMatcherUtility
{
    public static MethodMatcher createMethodMatcher(final String className, final List<Extension.Instrumentation.Pointcut.Method> methods, final Map<String, MethodMapper> classesToMethods, final String extName) throws XmlException {
        final List<MethodMatcher> matchers = new LinkedList<MethodMatcher>();
        for (final Extension.Instrumentation.Pointcut.Method method : methods) {
            try {
                matchers.add(createMethodMatcher(className, method, classesToMethods, extName));
            }
            catch (NoSuchMethodException e) {
                Agent.LOG.warning(e.getMessage());
            }
        }
        if (matchers.size() > 1) {
            return OrMethodMatcher.getMethodMatcher(matchers);
        }
        if (matchers.size() == 1) {
            return matchers.get(0);
        }
        throw new XmlException("All methods for " + className + " have already been added.");
    }
    
    public static MethodMatcher createMethodMatcher(final String className, final Extension.Instrumentation.Pointcut.Method method, final Map<String, MethodMapper> classesToMethods, final String extName) throws NoSuchMethodException, XmlException {
        if (method == null) {
            throw new XmlException("A method must be specified for a point cut in the extension.");
        }
        if (method.getReturnType() != null) {
            if (Utils.isPrimitiveType(method.getReturnType())) {
                throw new XmlException("The return type '" + method.getReturnType() + "' is not valid.  Primitive types are not allowed.");
            }
            final Type returnType = Type.getObjectType(method.getReturnType().replace('.', '/'));
            if (!ExtensionConversionUtility.isReturnTypeOkay(returnType)) {
                throw new XmlException("The return type '" + returnType.getClassName() + "' is not valid.  Primitive types are not allowed.");
            }
            return new ExactReturnTypeMethodMatcher(returnType);
        }
        else {
            validateMethod(method, extName);
            String methodName = method.getName();
            if (methodName == null) {
                throw new XmlException("A method name must be specified for a point cut in the extension.");
            }
            methodName = methodName.trim();
            if (methodName.length() == 0) {
                throw new XmlException("A method must be specified for a point cut in the extension.");
            }
            final Extension.Instrumentation.Pointcut.Method.Parameters mParams = method.getParameters();
            if (mParams == null || mParams.getType() == null) {
                if (!isDuplicateMethod(className, methodName, null, classesToMethods)) {
                    return new NameMethodMatcher(methodName);
                }
                throw new NoSuchMethodException("Method " + methodName + " has already been added to a point cut and will " + "not be added again.");
            }
            else {
                final String descriptor = MethodParameters.getDescriptor(mParams);
                if (descriptor == null) {
                    throw new XmlException("Descriptor not being calculated correctly.");
                }
                final String mDescriptor = descriptor.trim();
                if (!isDuplicateMethod(className, methodName, mDescriptor, classesToMethods)) {
                    return ExactParamsMethodMatcher.createExactParamsMethodMatcher(methodName, descriptor, className);
                }
                throw new NoSuchMethodException("Method " + methodName + " has already been added to a point cut and will " + "not be added again.");
            }
        }
    }
    
    private static void validateMethod(final Extension.Instrumentation.Pointcut.Method m, final String extName) throws XmlException {
        if (m == null) {
            throw new XmlException(MessageFormat.format("At least one method must be specified for each point cut in the extension {0}", extName));
        }
        final String mName = m.getName();
        if (mName == null || mName.trim().length() == 0) {
            throw new XmlException(MessageFormat.format("A method name must be specified for each method in the extension {0}", extName));
        }
    }
    
    private static boolean isDuplicateMethod(final String className, final String methodName, final String descriptor, final Map<String, MethodMapper> classesToMethods) {
        if (className != null) {
            final String name = Type.getObjectType(className).getClassName();
            MethodMapper mapper = classesToMethods.get(name);
            if (mapper == null) {
                mapper = new MethodMapper();
                classesToMethods.put(className, mapper);
            }
            return !mapper.addIfNotPresent(methodName, descriptor);
        }
        return true;
    }
}
