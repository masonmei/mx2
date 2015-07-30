// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.beans;

import java.util.Collections;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;

public class MethodParameters extends Extension.Instrumentation.Pointcut.Method.Parameters
{
    private final String paramDescriptor;
    private final boolean wasError;
    private final String errorMessage;
    
    public MethodParameters(final List<String> pParams) {
        if (pParams != null) {
            final List<Type> types = (List<Type>)Lists.newArrayListWithCapacity(pParams.size());
            for (final String p : pParams) {
                final Type t = new Type();
                t.setValue(p);
                types.add(t);
            }
            this.type = types;
        }
        String desc;
        boolean pError;
        String eMessage;
        try {
            desc = MethodConverterUtility.paramNamesToParamDescriptor(pParams);
            pError = false;
            eMessage = "";
        }
        catch (Exception e) {
            pError = true;
            eMessage = e.getMessage();
            desc = null;
        }
        this.paramDescriptor = desc;
        this.wasError = pError;
        this.errorMessage = eMessage;
    }
    
    public String getDescriptor() {
        return this.paramDescriptor;
    }
    
    public boolean isWasError() {
        return this.wasError;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public static String getDescriptor(final Extension.Instrumentation.Pointcut.Method.Parameters parameters) {
        return new MethodParameters((parameters == null) ? Collections.emptyList() : convertToStringList(parameters.getType())).getDescriptor();
    }
    
    private static List<String> convertToStringList(final List<Type> types) {
        final List<String> params = (List<String>)Lists.newArrayListWithCapacity(types.size());
        for (final Type t : types) {
            params.add(t.getValue());
        }
        return params;
    }
}
