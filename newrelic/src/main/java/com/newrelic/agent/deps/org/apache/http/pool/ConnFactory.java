// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.pool;

import java.io.IOException;

public interface ConnFactory<T, C>
{
    C create(T p0) throws IOException;
}
