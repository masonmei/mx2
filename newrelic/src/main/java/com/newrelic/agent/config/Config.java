// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

public interface Config
{
     <T> T getProperty(String p0);
    
     <T> T getProperty(String p0, T p1);
}
