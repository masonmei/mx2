// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import java.io.IOException;
import java.io.InputStream;

public interface ClassResolver
{
    InputStream getClassResource(String p0) throws IOException;
}
