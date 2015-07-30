// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.io;

import java.io.IOException;

@Deprecated
public interface OutputSupplier<T>
{
    T getOutput() throws IOException;
}
