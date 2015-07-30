// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.read;

import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;

public class ListAppender<E> extends AppenderBase<E>
{
    public List<E> list;
    
    public ListAppender() {
        this.list = new ArrayList<E>();
    }
    
    protected void append(final E e) {
        this.list.add(e);
    }
}
