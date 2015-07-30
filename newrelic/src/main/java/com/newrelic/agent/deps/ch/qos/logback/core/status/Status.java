// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import java.util.Iterator;

public interface Status
{
    public static final int INFO = 0;
    public static final int WARN = 1;
    public static final int ERROR = 2;
    
    int getLevel();
    
    int getEffectiveLevel();
    
    Object getOrigin();
    
    String getMessage();
    
    Throwable getThrowable();
    
    Long getDate();
    
    boolean hasChildren();
    
    void add(Status p0);
    
    boolean remove(Status p0);
    
    Iterator<Status> iterator();
}
