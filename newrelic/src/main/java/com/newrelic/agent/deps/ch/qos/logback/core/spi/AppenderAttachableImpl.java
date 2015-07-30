// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppenderAttachableImpl<E> implements AppenderAttachable<E>
{
    private final CopyOnWriteArrayList<Appender<E>> appenderList;
    static final long START;
    
    public AppenderAttachableImpl() {
        this.appenderList = new CopyOnWriteArrayList<Appender<E>>();
    }
    
    public void addAppender(final Appender<E> newAppender) {
        if (newAppender == null) {
            throw new IllegalArgumentException("Null argument disallowed");
        }
        this.appenderList.addIfAbsent(newAppender);
    }
    
    public int appendLoopOnAppenders(final E e) {
        int size = 0;
        for (final Appender<E> appender : this.appenderList) {
            appender.doAppend(e);
            ++size;
        }
        return size;
    }
    
    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.appenderList.iterator();
    }
    
    public Appender<E> getAppender(final String name) {
        if (name == null) {
            return null;
        }
        final Appender<E> found = null;
        for (final Appender<E> appender : this.appenderList) {
            if (name.equals(appender.getName())) {
                return appender;
            }
        }
        return null;
    }
    
    public boolean isAttached(final Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        for (final Appender<E> a : this.appenderList) {
            if (a == appender) {
                return true;
            }
        }
        return false;
    }
    
    public void detachAndStopAllAppenders() {
        for (final Appender<E> a : this.appenderList) {
            a.stop();
        }
        this.appenderList.clear();
    }
    
    public boolean detachAppender(final Appender<E> appender) {
        if (appender == null) {
            return false;
        }
        final boolean result = this.appenderList.remove(appender);
        return result;
    }
    
    public boolean detachAppender(final String name) {
        if (name == null) {
            return false;
        }
        boolean removed = false;
        for (final Appender<E> a : this.appenderList) {
            if (name.equals(a.getName())) {
                removed = this.appenderList.remove(a);
                break;
            }
        }
        return removed;
    }
    
    static {
        START = System.currentTimeMillis();
    }
}
