// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.util;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodMapper
{
    private final Map<String, List<String>> methods;
    
    public MethodMapper() {
        this.methods = new HashMap<String, List<String>>();
    }
    
    public void clear() {
        this.methods.clear();
    }
    
    public void addMethod(final String name, final List<String> descriptors) {
        List<String> descs = this.methods.get(name);
        if (descs == null) {
            descs = new ArrayList<String>(descriptors);
            this.methods.put(name, descs);
        }
        else {
            descs.addAll(descriptors);
        }
    }
    
    public boolean addIfNotPresent(final String name, final String descriptor) {
        List<String> descs = this.methods.get(name);
        if (descs == null) {
            descs = new ArrayList<String>();
            this.methods.put(name, descs);
        }
        if (!descs.contains(descriptor)) {
            descs.add(descriptor);
            return true;
        }
        return false;
    }
}
