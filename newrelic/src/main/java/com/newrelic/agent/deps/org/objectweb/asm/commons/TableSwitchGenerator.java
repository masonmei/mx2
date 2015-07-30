// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import com.newrelic.agent.deps.org.objectweb.asm.Label;

public interface TableSwitchGenerator
{
    void generateCase(int p0, Label p1);
    
    void generateDefault();
}
