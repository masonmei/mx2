// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.spi;

import java.util.Map;

public interface MDCAdapter
{
    void put(String p0, String p1);
    
    String get(String p0);
    
    void remove(String p0);
    
    void clear();
    
    Map getCopyOfContextMap();
    
    void setContextMap(Map p0);
}
