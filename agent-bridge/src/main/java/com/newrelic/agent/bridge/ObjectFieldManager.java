// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

public interface ObjectFieldManager
{
    void initializeFields(String p0, Object p1, Object p2);
    
    Object getFieldContainer(String p0, Object p1);
    
    void createClassObjectFields(String p0);
}
