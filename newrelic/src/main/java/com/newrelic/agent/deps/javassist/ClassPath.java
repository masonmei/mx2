// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist;

import java.net.URL;
import java.io.InputStream;

public interface ClassPath
{
    InputStream openClassfile(String p0) throws NotFoundException;
    
    URL find(String p0);
    
    void close();
}
