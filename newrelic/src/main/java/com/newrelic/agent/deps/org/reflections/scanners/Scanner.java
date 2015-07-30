// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.scanners;

import javax.annotation.Nullable;
import com.newrelic.agent.deps.org.reflections.vfs.Vfs;
import com.newrelic.agent.deps.com.google.common.base.Predicate;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import com.newrelic.agent.deps.org.reflections.Configuration;

public interface Scanner
{
    void setConfiguration(Configuration p0);
    
    Multimap<String, String> getStore();
    
    void setStore(Multimap<String, String> p0);
    
    Scanner filterResultsBy(Predicate<String> p0);
    
    boolean acceptsInput(String p0);
    
    Object scan(Vfs.File p0, @Nullable Object p1);
    
    boolean acceptResult(String p0);
}
