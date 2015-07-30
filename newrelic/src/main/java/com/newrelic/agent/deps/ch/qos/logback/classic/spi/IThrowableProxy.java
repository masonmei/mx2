// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

public interface IThrowableProxy
{
    String getMessage();
    
    String getClassName();
    
    StackTraceElementProxy[] getStackTraceElementProxyArray();
    
    int getCommonFrames();
    
    IThrowableProxy getCause();
    
    IThrowableProxy[] getSuppressed();
}
