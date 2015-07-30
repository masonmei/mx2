// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.util;

import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.AnnotationMethodMatcher;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.instrumentation.tracing.ParameterAttributeName;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.instrumentation.classmatchers.AllClassesMatcher;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.newrelic.agent.instrumentation.InstrumentationType;
import java.util.HashMap;
import java.util.ArrayList;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.List;
import com.newrelic.agent.extension.beans.Extension;

public final class ExtensionConversionUtility
{
    public static final String DEFAULT_CONFIG_DIRECTORY = "extensions";
    
    public static void validateExtensionAttributes(final Extension extension) throws XmlException {
        if (extension == null) {
            throw new XmlException("There must be an extension to instrument new methods.\n");
        }
        if (extension.getName() == null || extension.getName().length() == 0) {
            throw new XmlException("The extension must have a name attribute.\n");
        }
        if (extension.getVersion() < 0.0) {
            throw new XmlException(" The version number must be a double and must be greater than or equal to 0.\n");
        }
    }
    
    private static void validateInstrument(final Extension.Instrumentation instrument) throws XmlException {
        if (instrument == null) {
            throw new XmlException("In order to provide instrumentation, there must be an instrument tag.\n");
        }
        final List<Extension.Instrumentation.Pointcut> pcs = instrument.getPointcut();
        if (pcs == null || pcs.isEmpty()) {
            throw new XmlException("A point cut tag is required to instrument a method.\n");
        }
    }
    
    public static List<ExtensionClassAndMethodMatcher> convertToPointCutsForValidation(final Extension ext) throws XmlException {
        final List<ExtensionClassAndMethodMatcher> pointCutsOut = new ArrayList<ExtensionClassAndMethodMatcher>();
        final Extension.Instrumentation inst = ext.getInstrumentation();
        validateExtensionAttributes(ext);
        validateInstrument(inst);
        final List<Extension.Instrumentation.Pointcut> pcs = inst.getPointcut();
        final String defaultMetricPrefix = createDefaultMetricPrefix(inst, true);
        final Map<String, MethodMapper> classesToMethods = new HashMap<String, MethodMapper>();
        for (final Extension.Instrumentation.Pointcut pc : pcs) {
            pointCutsOut.add(createPointCut(ext, pc, defaultMetricPrefix, ext.getName(), classesToMethods, true, InstrumentationType.LocalCustomXml, false));
        }
        return pointCutsOut;
    }
    
    public static List<ExtensionClassAndMethodMatcher> convertToEnabledPointCuts(final Collection<Extension> extensions, final boolean custom, final InstrumentationType type) {
        return convertToEnabledPointCuts(extensions, custom, type, true);
    }
    
    public static List<ExtensionClassAndMethodMatcher> convertToEnabledPointCuts(final Collection<Extension> extensions, final boolean custom, final InstrumentationType type, final boolean isAttsEnabled) {
        final List<ExtensionClassAndMethodMatcher> pointCutsOut = new ArrayList<ExtensionClassAndMethodMatcher>();
        if (extensions != null) {
            final Map<String, MethodMapper> classesToMethods = new HashMap<String, MethodMapper>();
            for (final Extension ext : extensions) {
                if (ext.isEnabled()) {
                    pointCutsOut.addAll(convertToEnabledPointCuts(ext, ext.getName(), classesToMethods, custom, type, isAttsEnabled));
                }
                else {
                    Agent.LOG.log(Level.WARNING, MessageFormat.format("Extension {0} is not enabled and so will not be instrumented.", ext.getName()));
                }
            }
        }
        return pointCutsOut;
    }
    
    private static List<ExtensionClassAndMethodMatcher> convertToEnabledPointCuts(final Extension extension, final String extensionName, final Map<String, MethodMapper> classesToMethods, final boolean custom, final InstrumentationType type, final boolean isAttsEnabled) {
        final List<ExtensionClassAndMethodMatcher> pointCutsOut = new ArrayList<ExtensionClassAndMethodMatcher>();
        if (extension != null) {
            final String defaultMetricPrefix = createDefaultMetricPrefix(extension.getInstrumentation(), custom);
            final List<Extension.Instrumentation.Pointcut> inCuts = extension.getInstrumentation().getPointcut();
            if (inCuts != null && !inCuts.isEmpty()) {
                for (final Extension.Instrumentation.Pointcut cut : inCuts) {
                    try {
                        final ExtensionClassAndMethodMatcher pc = createPointCut(extension, cut, defaultMetricPrefix, extensionName, classesToMethods, custom, type, isAttsEnabled);
                        if (pc == null) {
                            continue;
                        }
                        logPointCutCreation(pc);
                        pointCutsOut.add(pc);
                    }
                    catch (Exception e) {
                        final String msg = MessageFormat.format("An error occurred reading in a pointcut in extension {0} : {1}", extensionName, e.toString());
                        Agent.LOG.log(Level.SEVERE, msg);
                        Agent.LOG.log(Level.FINER, msg, e);
                    }
                }
            }
            else {
                final String msg2 = MessageFormat.format("There were no point cuts in the extension {0}.", extensionName);
                Agent.LOG.log(Level.INFO, msg2);
            }
        }
        return pointCutsOut;
    }
    
    private static String createDefaultMetricPrefix(final Extension.Instrumentation instrument, final boolean custom) {
        String metricPrefix = custom ? "Custom" : "Java";
        if (instrument != null) {
            final String prefix = instrument.getMetricPrefix();
            if (prefix != null && prefix.length() != 0) {
                metricPrefix = prefix;
            }
        }
        return metricPrefix;
    }
    
    private static void logPointCutCreation(final ExtensionClassAndMethodMatcher pc) {
        final String msg = MessageFormat.format("Extension instrumentation point: {0} {1}", pc.getClassMatcher(), pc.getMethodMatcher());
        Agent.LOG.finest(msg);
    }
    
    private static ExtensionClassAndMethodMatcher createPointCut(final Extension extension, final Extension.Instrumentation.Pointcut cut, final String metricPrefix, final String pName, final Map<String, MethodMapper> classesToMethods, final boolean custom, final InstrumentationType type, final boolean isAttsEnabled) throws XmlException {
        ClassMatcher classMatcher;
        if (cut.getMethodAnnotation() != null) {
            classMatcher = new AllClassesMatcher();
        }
        else {
            classMatcher = createClassMatcher(cut, pName);
        }
        final MethodMatcher methodMatcher = createMethodMatcher(cut, pName, classesToMethods);
        List<ParameterAttributeName> reportedParams = null;
        if (!isAttsEnabled) {
            reportedParams = (List<ParameterAttributeName>)Lists.newArrayList();
        }
        else {
            reportedParams = getParameterAttributeNames(cut.getMethod());
        }
        return new ExtensionClassAndMethodMatcher(extension, cut, metricPrefix, classMatcher, methodMatcher, custom, reportedParams, type);
    }
    
    private static List<ParameterAttributeName> getParameterAttributeNames(final List<Extension.Instrumentation.Pointcut.Method> methods) {
        final List<ParameterAttributeName> reportedParams = (List<ParameterAttributeName>)Lists.newArrayList();
        for (final Extension.Instrumentation.Pointcut.Method m : methods) {
            if (m.getParameters() != null && m.getParameters().getType() != null) {
                for (int i = 0; i < m.getParameters().getType().size(); ++i) {
                    final Extension.Instrumentation.Pointcut.Method.Parameters.Type t = m.getParameters().getType().get(i);
                    if (t.getAttributeName() != null) {
                        try {
                            final MethodMatcher methodMatcher = MethodMatcherUtility.createMethodMatcher("DummyClassName", m, (Map<String, MethodMapper>)Maps.newHashMap(), "");
                            final ParameterAttributeName reportedParam = new ParameterAttributeName(i, t.getAttributeName(), methodMatcher);
                            reportedParams.add(reportedParam);
                        }
                        catch (Exception e) {
                            Agent.LOG.log(Level.FINEST, (Throwable)e, e.getMessage(), new Object[0]);
                        }
                    }
                }
            }
        }
        return reportedParams;
    }
    
    private static MethodMatcher createMethodMatcher(final Extension.Instrumentation.Pointcut cut, final String pExtName, final Map<String, MethodMapper> classesToMethods) throws XmlException {
        final List<Extension.Instrumentation.Pointcut.Method> methods = cut.getMethod();
        if (methods != null && !methods.isEmpty()) {
            return MethodMatcherUtility.createMethodMatcher(getClassName(cut), methods, classesToMethods, pExtName);
        }
        if (cut.getMethodAnnotation() != null) {
            return new AnnotationMethodMatcher(Type.getObjectType(cut.getMethodAnnotation().replace('.', '/')));
        }
        throw new XmlException(MessageFormat.format("At least one method must be specified for each point cut in the extension {0}", pExtName));
    }
    
    static boolean isReturnTypeOkay(final Type returnType) {
        if (returnType.getSort() == 9) {
            return isReturnTypeOkay(returnType.getElementType());
        }
        return returnType.getSort() == 10;
    }
    
    public static String getClassName(final Extension.Instrumentation.Pointcut cut) {
        if (cut.getClassName() != null) {
            return cut.getClassName().getValue().trim();
        }
        if (cut.getInterfaceName() != null) {
            return cut.getInterfaceName().trim();
        }
        return null;
    }
    
    static ClassMatcher createClassMatcher(final Extension.Instrumentation.Pointcut pointcut, final String pExtName) throws XmlException {
        final Extension.Instrumentation.Pointcut.ClassName className = pointcut.getClassName();
        if (className != null) {
            if (className.getValue() == null || className.getValue().isEmpty()) {
                throw new XmlException("");
            }
            if (className.isIncludeSubclasses()) {
                return new ChildClassMatcher(className.getValue(), false);
            }
            return new ExactClassMatcher(className.getValue());
        }
        else {
            if (pointcut.getInterfaceName() != null) {
                return new InterfaceMatcher(pointcut.getInterfaceName());
            }
            throw new XmlException(MessageFormat.format("A class name, interface name, or super class name needs to be specified for every point cut in the extension {0}", pExtName));
        }
    }
}
