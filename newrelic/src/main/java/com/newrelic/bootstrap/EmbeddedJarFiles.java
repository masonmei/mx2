// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.bootstrap;

import java.io.IOException;
import java.io.File;

public interface EmbeddedJarFiles
{
    String[] getEmbeddedAgentJarFileNames();
    
    File getJarFileInAgent(String p0) throws IOException;
}
