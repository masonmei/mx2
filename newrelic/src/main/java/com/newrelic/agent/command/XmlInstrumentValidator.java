// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.command;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.lang.reflect.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import com.newrelic.agent.extension.beans.Extension;
import com.newrelic.agent.extension.util.ExtensionConversionUtility;
import com.newrelic.agent.extension.dom.ExtensionDomParser;
import java.io.File;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.text.MessageFormat;
import com.newrelic.agent.deps.org.apache.commons.cli.CommandLine;

public class XmlInstrumentValidator
{
    public static void validateInstrumentation(final CommandLine cmd) {
        final XmlInstrumentParams params = new XmlInstrumentParams();
        if (cmd == null) {
            printMessage("There were no command line parameters.");
        }
        else {
            try {
                if (verifyAndSetParameters(cmd, params)) {
                    validateInstrumentation(params);
                    printMessage(MessageFormat.format("PASS: The extension at {0} was successfully validated.", params.getFile().getAbsoluteFile()));
                }
            }
            catch (IOException e) {
                printMessage(MessageFormat.format("FAIL: The extension at {0} failed validation. \nReason: {1} \nNOTE: set debug to true for more information.", params.getFile().getAbsoluteFile(), e.getMessage()));
            }
            catch (SAXException e2) {
                printMessage(MessageFormat.format("FAIL: The extension at {0} failed validation. \nReason: {1} \nNOTE: set debug to true for more information.", params.getFile().getAbsoluteFile(), e2.getMessage()));
            }
            catch (IllegalArgumentException e3) {
                final File file = params.getFile();
                String fileName = null;
                if (file != null) {
                    fileName = file.getAbsolutePath();
                }
                printMessage(MessageFormat.format("FAIL: The extension at {0} failed validation. \n Reason: {1} \n Note: Set debug to true for more information.", fileName, e3.getMessage()));
            }
            catch (RuntimeException e4) {
                printMessage(MessageFormat.format("FAIL: The extension at {0} failed validation. \n Reason: {1} \n Note: Set debug to true for more information.", params.getFile().getAbsoluteFile(), e4.getMessage()));
            }
            catch (ClassNotFoundException e5) {
                printMessage(MessageFormat.format("FAIL: The extension at {0} failed validation. \n Reason: The following class was not found: {1} \n Note: Set debug to true for more information.", params.getFile().getAbsoluteFile(), e5.getMessage()));
            }
            catch (Exception e6) {
                printMessage(MessageFormat.format("FAIL: The extension at {0} failed validation. \n Reason: {1} \n Note: Set debug to true for more information.", params.getFile().getAbsoluteFile(), e6.getMessage()));
            }
        }
    }
    
    protected static void validateInstrumentation(final XmlInstrumentParams params) throws Exception {
        final Extension extension = ExtensionDomParser.readFile(params.getFile());
        if (params.isDebug()) {
            System.out.println("Xml was successfully read. Starting processing.");
        }
        final List<ExtensionClassAndMethodMatcher> convertedPcs = ExtensionConversionUtility.convertToPointCutsForValidation(extension);
        final Extension.Instrumentation inst = extension.getInstrumentation();
        if (inst == null) {
            throw new RuntimeException("The instrumentation propery must be set for the extension.");
        }
        final List<Extension.Instrumentation.Pointcut> origPcs = inst.getPointcut();
        if (convertedPcs.size() != origPcs.size()) {
            throw new IllegalArgumentException("The processed number of point cuts does not match theoriginal number of point cuts in the xml. Remove duplicates.");
        }
        for (int i = 0; i < convertedPcs.size(); ++i) {
            final MethodHolder holder = sortData(origPcs.get(i), params.isDebug());
            verifyPointCut(convertedPcs.get(i), holder);
            verifyAllMethodsAccounted(holder);
        }
    }
    
    private static boolean verifyAndSetParameters(final CommandLine cmd, final XmlInstrumentParams params) throws IllegalArgumentException {
        try {
            final XmlInstrumentOptions[] arr$;
            final XmlInstrumentOptions[] options = arr$ = XmlInstrumentOptions.values();
            for (final XmlInstrumentOptions ops : arr$) {
                ops.validateAndAddParameter(params, cmd.getOptionValues(ops.getFlagName()), ops.getFlagName());
            }
            return true;
        }
        catch (Exception e) {
            printMessage(MessageFormat.format("FAIL: The command line parameters are invalid. \n Reason: {0}", e.getMessage()));
            return false;
        }
    }
    
    private static void verifyAllMethodsAccounted(final MethodHolder originals) {
        if (originals.hasMethods()) {
            throw new IllegalArgumentException(MessageFormat.format("These methods are either duplicates, constructors, or are not present in the class: {0}", originals.getCurrentMethods()));
        }
    }
    
    private static void verifyPointCut(final ExtensionClassAndMethodMatcher cut, final MethodHolder origMethods) throws ClassNotFoundException {
        if (cut != null) {
            final Collection<String> classNames = cut.getClassMatcher().getClassNames();
            for (final String name : classNames) {
                final String nameDoted = name.replace("/", ".").trim();
                final Class<?> theClass = Thread.currentThread().getContextClassLoader().loadClass(nameDoted);
                validateNoInterface(theClass);
                checkMethods(cut.getMethodMatcher(), theClass.getDeclaredMethods(), origMethods);
            }
        }
    }
    
    private static void validateNoInterface(final Class theClass) {
        if (theClass.isInterface()) {
            throw new IllegalArgumentException(MessageFormat.format("Only classes can be implemented. This class is an interface: {0}", theClass.getName()));
        }
    }
    
    private static void checkMethods(final MethodMatcher matcher, final Method[] classMethods, final MethodHolder origMethods) {
        if (classMethods != null) {
            if (origMethods == null) {
                throw new IllegalArgumentException("Instrumenting a class not found in the XML.");
            }
            for (final Method m : classMethods) {
                final String currentDesc = Type.getMethodDescriptor(m);
                checkPresenceAndMatcher(m.getName(), currentDesc, matcher, origMethods);
            }
        }
    }
    
    private static void checkPresenceAndMatcher(final String currentName, final String currentDesc, final MethodMatcher matcher, final MethodHolder origMethods) {
        if (origMethods.isMethodPresent(currentName, currentDesc, true) && !matcher.matches(-1, currentName, currentDesc, MethodMatcher.UNSPECIFIED_ANNOTATIONS)) {
            throw new IllegalArgumentException(MessageFormat.format("The method was in the point cut but did not match the method matcher. Name: {0} Desc: {1}", currentName, currentDesc));
        }
    }
    
    private static MethodHolder sortData(final Extension.Instrumentation.Pointcut pc, final boolean debug) {
        final MethodHolder cMethods = new MethodHolder(debug);
        if (pc != null) {
            cMethods.addMethods(pc.getMethod());
        }
        return cMethods;
    }
    
    protected static void printMessage(final String msg) {
        System.out.println(msg);
    }
}
