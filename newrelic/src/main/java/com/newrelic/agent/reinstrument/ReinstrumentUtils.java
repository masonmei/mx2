// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.reinstrument;

import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.extension.util.MethodMapper;
import com.newrelic.agent.extension.util.MethodMatcherUtility;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.extension.beans.MethodParameters;
import java.net.URL;
import java.io.IOException;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.extension.util.ExtensionConversionUtility;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.HashSet;
import java.util.Set;
import com.newrelic.agent.extension.beans.Extension;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.List;

public class ReinstrumentUtils
{
    public static void checkClassExistsAndRetransformClasses(final ReinstrumentResult result, final List<ExtensionClassAndMethodMatcher> pcs, final Extension ext, final Set<Class<?>> classesToRetransform) {
        if (!pcs.isEmpty()) {
            final Set<ClassLoader> loaders = new HashSet<ClassLoader>();
            final Map<String, Class<?>> toRetransform = Maps.newHashMap();
            getLoadedClassData(pcs, loaders, toRetransform);
            checkInputClasses(result, loaders, ext, toRetransform);
        }
        retransform(result, classesToRetransform);
    }
    
    private static void getLoadedClassData(final List<ExtensionClassAndMethodMatcher> pcs, final Set<ClassLoader> loaders, final Map<String, Class<?>> toRetransform) {
        final Class<?>[] allLoadedClasses = (Class<?>[])ServiceFactory.getAgent().getInstrumentation().getAllLoadedClasses();
        if (allLoadedClasses != null) {
            for (final Class<?> current : allLoadedClasses) {
                try {
                    if (current != null) {
                        if (current.getClassLoader() != null) {
                            loaders.add(current.getClassLoader());
                        }
                        if (shouldTransform(current, pcs)) {
                            toRetransform.put(current.getName(), current);
                        }
                    }
                }
                catch (Exception e) {
                    Agent.LOG.log(Level.FINE, "An unexpected exception occured examining a class for retransformation.");
                    if (Agent.LOG.isFinestEnabled()) {
                        Agent.LOG.log(Level.FINEST, "An exception occured examining a class for retransformation.", e);
                    }
                }
            }
        }
    }
    
    public static void retransform(final ReinstrumentResult result, final Set<Class<?>> classesToRetransform) {
        try {
            if (!classesToRetransform.isEmpty()) {
                ServiceFactory.getAgent().getInstrumentation().retransformClasses((Class<?>[])classesToRetransform.toArray(new Class[classesToRetransform.size()]));
                result.setRetranformedInitializedClasses(getClassNames(classesToRetransform));
            }
        }
        catch (Exception e) {
            handleError(result, MessageFormat.format("Attempt to retransform classes failed. Message: {0}", e.getMessage()), e);
        }
    }
    
    private static Set<String> getClassNames(final Set<Class<?>> classes) {
        final Set<String> names = Sets.newHashSet();
        for (final Class<?> clazz : classes) {
            names.add(clazz.getName());
        }
        return names;
    }
    
    private static void performRetransformations(final ReinstrumentResult result, final Map<String, Class<?>> toRetransform) {
        try {
            final int size = toRetransform.size();
            if (size > 0) {
                ServiceFactory.getAgent().getInstrumentation().retransformClasses((Class<?>[])toRetransform.values().toArray(new Class[size]));
                result.setRetranformedInitializedClasses(toRetransform.keySet());
            }
        }
        catch (Exception e) {
            handleError(result, MessageFormat.format("Attempt to retransform classes failed. Message: {0}", e.getMessage()), e);
        }
    }
    
    private static void handleError(final ReinstrumentResult result, final String msg, final Exception e) {
        result.addErrorMessage(msg);
        Agent.LOG.log(Level.INFO, msg);
        if (Agent.LOG.isFinestEnabled()) {
            Agent.LOG.log(Level.FINEST, msg, e);
        }
    }
    
    protected static void handleErrorPartialInstrumentation(final ReinstrumentResult result, final List<Exception> msgs, final String pXml) {
        if (msgs != null && msgs.size() > 0) {
            for (final Exception msg : msgs) {
                result.addErrorMessage(msg.getMessage());
                Agent.LOG.log(Level.INFO, msg.getMessage());
            }
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, MessageFormat.format("Errors occured when processing this xml: {0}", pXml));
            }
        }
    }
    
    protected static void handleErrorPartialInstrumentation(final ReinstrumentResult result, final String msg) {
        Agent.LOG.log(Level.INFO, msg);
        result.addErrorMessage(msg);
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.log(Level.FINER, MessageFormat.format("Errors occured when processing this xml: {0}", msg));
        }
    }
    
    protected static void checkInputClasses(final ReinstrumentResult result, final Set<ClassLoader> loaders, final Extension ext, final Map<String, Class<?>> toRetransform) {
        if (ext.getInstrumentation() != null) {
            final List<Extension.Instrumentation.Pointcut> pcs = ext.getInstrumentation().getPointcut();
            for (final Extension.Instrumentation.Pointcut pointcut : pcs) {
                if (pointcut.getMethodAnnotation() != null) {
                    continue;
                }
                checkForClassAndMethods(result, loaders, ExtensionConversionUtility.getClassName(pointcut), toRetransform, pointcut);
            }
        }
    }
    
    private static void checkForClassAndMethods(final ReinstrumentResult result, final Set<ClassLoader> loaders, final String className, final Map<String, Class<?>> toRetransform, final Extension.Instrumentation.Pointcut pc) {
        if (className != null) {
            final Class<?> current = toRetransform.get(className);
            if (current != null) {
                checkMethodsInClass(result, ClassStructure.getClassStructure(current), pc);
            }
            else {
                for (final ClassLoader loader : loaders) {
                    if (loader != null) {
                        final URL resource = loader.getResource(className.replace(".", "/") + ".class");
                        if (resource == null) {
                            continue;
                        }
                        try {
                            checkMethodsInClass(result, ClassStructure.getClassStructure(resource), pc);
                            return;
                        }
                        catch (IOException e) {
                            Agent.LOG.log(Level.FINER, "Error validating class " + className, e);
                        }
                    }
                }
                handleErrorPartialInstrumentation(result, MessageFormat.format("The class {0} does not match a loaded class in the JVM. Either the class has not been loaded yet or it does not exist.", className));
            }
        }
    }
    
    private static void checkMethodsInClass(final ReinstrumentResult result, final ClassStructure classStructure, final Extension.Instrumentation.Pointcut pc) {
        final List<Extension.Instrumentation.Pointcut.Method> desiredMethods = pc.getMethod();
        if (desiredMethods != null) {
            final Set<Method> actualMethods = classStructure.getMethods();
            for (final Extension.Instrumentation.Pointcut.Method m : desiredMethods) {
                if (!foundMethod(m, actualMethods)) {
                    handleErrorPartialInstrumentation(result, MessageFormat.format("The method {0} with parameter type {1} on class {2} is not present and therefore will never match anything.", m.getName(), MethodParameters.getDescriptor(m.getParameters()), ExtensionConversionUtility.getClassName(pc)));
                }
            }
        }
    }
    
    private static boolean foundMethod(final Extension.Instrumentation.Pointcut.Method method, final Set<Method> actualMethods) {
        try {
            final MethodMatcher methodMatcher = MethodMatcherUtility.createMethodMatcher("BogusClass", method, Maps.<String,MethodMapper>newHashMap(), "");
            for (final Method m : actualMethods) {
                if (methodMatcher.matches(-1, m.getName(), m.getDescriptor(), MethodMatcher.UNSPECIFIED_ANNOTATIONS)) {
                    return true;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    private static boolean shouldTransform(final Class<?> clazz, final List<ExtensionClassAndMethodMatcher> newPcs) {
        for (final ExtensionClassAndMethodMatcher pc : newPcs) {
            if (pc.getClassMatcher().isMatch(clazz)) {
                return true;
            }
        }
        return false;
    }
}
