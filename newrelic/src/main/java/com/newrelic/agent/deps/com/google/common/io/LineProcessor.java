// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.io;

import java.io.IOException;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface LineProcessor<T>
{
    boolean processLine(String p0) throws IOException;
    
    T getResult();
}
