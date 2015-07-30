// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.io.Writer;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class ProfileSegment implements JSONStreamAware
{
    private final ProfiledMethod method;
    private int runnableCallCount;
    private int nonrunnableCallCount;
    private final Map<ProfiledMethod, ProfileSegment> children;
    
    private ProfileSegment(final ProfiledMethod method) {
        this.runnableCallCount = 0;
        this.nonrunnableCallCount = 0;
        this.children = Maps.newIdentityHashMap();
        this.method = method;
    }
    
    public static ProfileSegment newProfileSegment(final ProfiledMethod method) {
        if (method == null) {
            return null;
        }
        return new ProfileSegment(method);
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        JSONArray.writeJSONString(Arrays.asList(this.method, this.runnableCallCount, this.nonrunnableCallCount, new ArrayList(this.children.values())), out);
    }
    
    public String toString() {
        return this.method.toString();
    }
    
    public ProfiledMethod getMethod() {
        return this.method;
    }
    
    protected int getRunnableCallCount() {
        return this.runnableCallCount;
    }
    
    public void incrementRunnableCallCount() {
        ++this.runnableCallCount;
    }
    
    public void incrementNonRunnableCallCount() {
        ++this.nonrunnableCallCount;
    }
    
    Collection<ProfileSegment> getChildren() {
        return this.children.values();
    }
    
    Map<ProfiledMethod, ProfileSegment> getChildMap() {
        return this.children;
    }
    
    ProfileSegment addChild(final ProfiledMethod method) {
        ProfileSegment result = this.children.get(method);
        if (result == null) {
            result = newProfileSegment(method);
            this.children.put(method, result);
        }
        return result;
    }
    
    void removeChild(final ProfiledMethod method) {
        this.children.remove(method);
    }
    
    public int getCallSiteCount() {
        int count = 1;
        for (final ProfileSegment segment : this.children.values()) {
            count += segment.getCallSiteCount();
        }
        return count;
    }
    
    public int getCallCount(final ProfiledMethod method) {
        int count = method.equals(this.getMethod()) ? this.runnableCallCount : 0;
        for (final ProfileSegment kid : this.children.values()) {
            count += kid.getCallCount(method);
        }
        return count;
    }
    
    public Set<ProfiledMethod> getMethods() {
        final Set<ProfiledMethod> methods = new HashSet<ProfiledMethod>();
        methods.add(this.getMethod());
        for (final ProfileSegment kid : this.children.values()) {
            methods.addAll(kid.getMethods());
        }
        return methods;
    }
}
