// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.command;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import com.newrelic.agent.extension.beans.MethodParameters;
import com.newrelic.agent.extension.beans.Extension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MethodHolder
{
    private final Map<String, List<String>> nameToMethods;
    private final boolean isDebug;
    
    public MethodHolder(final boolean pDebug) {
        this.nameToMethods = new HashMap<String, List<String>>();
        this.isDebug = pDebug;
    }
    
    protected void addMethods(final List<Extension.Instrumentation.Pointcut.Method> methods) {
        if (methods != null) {
            for (final Extension.Instrumentation.Pointcut.Method m : methods) {
                if (m != null && m.getParameters() != null) {
                    this.addMethod(m.getName(), MethodParameters.getDescriptor(m.getParameters()));
                }
            }
        }
    }
    
    private void addMethod(String name, String descr) {
        name = name.trim();
        descr = descr.trim();
        List<String> value = this.nameToMethods.get(name);
        if (value == null) {
            value = new ArrayList<String>();
            this.nameToMethods.put(name, value);
        }
        if (!value.contains(descr)) {
            value.add(descr);
        }
    }
    
    protected boolean isMethodPresent(String name, String descr, final boolean remove) {
        name = name.trim();
        descr = descr.trim();
        final List<String> value = this.nameToMethods.get(name);
        if (value != null) {
            if (this.isDebug) {
                XmlInstrumentValidator.printMessage(MessageFormat.format("Found the method {0} from the xml in the list of class methods. Checking method parameters.", name));
            }
            final Iterator<String> it = value.iterator();
            while (it.hasNext()) {
                final String xmlDesc = it.next();
                if (descr.startsWith(xmlDesc)) {
                    XmlInstrumentValidator.printMessage(MessageFormat.format("Matched Method: {0} {1}", name, descr));
                    if (remove) {
                        it.remove();
                        if (value.isEmpty()) {
                            this.nameToMethods.remove(name);
                        }
                    }
                    return true;
                }
                if (!this.isDebug) {
                    continue;
                }
                XmlInstrumentValidator.printMessage(MessageFormat.format("Descriptors for method {0} did not match. Xml descriptor: {1}, Method descriptor: {2} ", name, xmlDesc, descr));
            }
        }
        return false;
    }
    
    protected boolean hasMethods() {
        final Iterator<Map.Entry<String, List<String>>> it = this.nameToMethods.entrySet().iterator();
        return it.hasNext();
    }
    
    protected String getCurrentMethods() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, List<String>> values : this.nameToMethods.entrySet()) {
            final List<String> descriptors = values.getValue();
            if (descriptors != null && !descriptors.isEmpty()) {
                sb.append("\nMethod Name: ");
                sb.append(values.getKey());
                sb.append(" Param Descriptors: ");
                for (final String v : descriptors) {
                    sb.append(v);
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
}
