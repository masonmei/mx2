// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.sift;

import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;

public interface AppenderTracker<E>
{
    public static final int THRESHOLD = 1800000;
    
    void put(String p0, Appender<E> p1, long p2);
    
    Appender<E> get(String p0, long p1);
    
    void stopStaleAppenders(long p0);
    
    List<String> keyList();
    
    List<Appender<E>> valueList();
    
    void stopAndRemoveNow(String p0);
}
