// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.pool;

import java.util.concurrent.Future;
import com.newrelic.agent.deps.org.apache.http.concurrent.FutureCallback;

public interface ConnPool<T, E>
{
    Future<E> lease(T p0, Object p1, FutureCallback<E> p2);
    
    void release(E p0, boolean p1);
}
