// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class ProfileTree implements JSONStreamAware
{
    private final Map<ProfiledMethod, ProfileSegment> rootSegments;
    private final Map<StackTraceElement, ProfiledMethod> profiledMethods;
    private long cpuTime;
    
    public ProfileTree() {
        this.rootSegments = (Map<ProfiledMethod, ProfileSegment>)Maps.newIdentityHashMap();
        this.profiledMethods = (Map<StackTraceElement, ProfiledMethod>)Maps.newHashMap();
    }
    
    private ProfileSegment add(final StackTraceElement stackTraceElement, final ProfileSegment parent, final boolean runnable) {
        ProfiledMethod method = this.profiledMethods.get(stackTraceElement);
        if (method == null) {
            method = ProfiledMethod.newProfiledMethod(stackTraceElement);
            if (method != null) {
                this.profiledMethods.put(stackTraceElement, method);
            }
        }
        if (method == null) {
            return parent;
        }
        return this.add(method, parent, runnable);
    }
    
    private ProfileSegment add(final ProfiledMethod method, final ProfileSegment parent, final boolean runnable) {
        final ProfileSegment result = this.add(method, parent);
        if (runnable) {
            result.incrementRunnableCallCount();
        }
        else {
            result.incrementNonRunnableCallCount();
        }
        return result;
    }
    
    private ProfileSegment add(final ProfiledMethod method, final ProfileSegment parent) {
        ProfileSegment result;
        if (parent == null) {
            result = this.rootSegments.get(method);
            if (result == null) {
                result = ProfileSegment.newProfileSegment(method);
                this.rootSegments.put(method, result);
            }
        }
        else {
            result = parent.addChild(method);
        }
        return result;
    }
    
    public int getCallCount(final StackTraceElement stackElement) {
        final ProfiledMethod method = ProfiledMethod.newProfiledMethod(stackElement);
        if (method == null) {
            return 0;
        }
        int count = 0;
        for (final ProfileSegment segment : this.rootSegments.values()) {
            count += segment.getCallCount(method);
        }
        return count;
    }
    
    public int getCallSiteCount() {
        int count = 0;
        for (final ProfileSegment segment : this.rootSegments.values()) {
            count += segment.getCallSiteCount();
        }
        return count;
    }
    
    public Collection<ProfileSegment> getRootSegments() {
        return this.rootSegments.values();
    }
    
    public int getRootCount() {
        return this.getRootSegments().size();
    }
    
    public int getMethodCount() {
        final Set<ProfiledMethod> methodNames = new HashSet<ProfiledMethod>();
        for (final ProfileSegment segment : this.rootSegments.values()) {
            methodNames.addAll(segment.getMethods());
        }
        return methodNames.size();
    }
    
    public void addStackTrace(final List<StackTraceElement> stackTraceList, final boolean runnable) {
        ProfileSegment parent = null;
        for (final StackTraceElement methodCall : stackTraceList) {
            parent = this.add(methodCall, parent, runnable);
        }
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        final Collection<ProfileSegment> rootSegments = this.getRootSegments();
        final ArrayList<Object> list = Lists.newArrayListWithCapacity(rootSegments.size() + 1);
        list.add(this.getExtraData());
        list.addAll(rootSegments);
        JSONArray.writeJSONString(list, out);
    }
    
    private Map<String, Object> getExtraData() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("cpu_time", this.cpuTime);
        return data;
    }
    
    public void incrementCpuTime(final long cpuTime) {
        this.cpuTime += cpuTime;
    }
    
    public long getCpuTime() {
        return this.cpuTime;
    }
    
    public void setMethodDetails(final Map<String, Class<?>> classMap) {
        for (final ProfiledMethod method : this.profiledMethods.values()) {
            method.setMethodDetails(classMap);
        }
    }
}
