// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.HashMap;
import java.util.Iterator;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Vector;
import java.util.Arrays;
import java.util.Map;

public class RubyConversion
{
    private static final Map<String, String> rubyToJavaClassMap;
    
    public static Class<Exception> rubyClassToJavaClass(final String fullClassName) throws ClassNotFoundException {
        String className = RubyConversion.rubyToJavaClassMap.get(fullClassName);
        if (className != null) {
            return (Class<Exception>)Class.forName(className);
        }
        try {
            final Vector<String> typeParts = new Vector<String>(Arrays.asList(fullClassName.split("::")));
            if (typeParts.size() < 1) {
                throw new ClassNotFoundException(MessageFormat.format("Unable to load class {0}", fullClassName));
            }
            className = typeParts.lastElement();
            typeParts.remove(className);
            final StringBuilder packageName = new StringBuilder();
            for (String typePart : typeParts) {
                if ("NewRelic".equals(typePart)) {
                    typePart = "com.newrelic";
                }
                packageName.append(typePart).append('.');
            }
            className = packageName.toString().toLowerCase() + className;
            return (Class<Exception>)Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            Agent.LOG.severe(MessageFormat.format("Unable to deserialize class {0}", fullClassName));
            throw e;
        }
    }
    
    static {
        rubyToJavaClassMap = new HashMap<String, String>() {
            private static final long serialVersionUID = -2335806597139433736L;
            
            {
                this.put("RuntimeError", "java.lang.RuntimeException");
            }
        };
    }
}
