// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;

public interface AppenderAttachable<E>
{
    void addAppender(Appender<E> p0);
    
    Iterator<Appender<E>> iteratorForAppenders();
    
    Appender<E> getAppender(String p0);
    
    boolean isAttached(Appender<E> p0);
    
    void detachAndStopAllAppenders();
    
    boolean detachAppender(Appender<E> p0);
    
    boolean detachAppender(String p0);
}
