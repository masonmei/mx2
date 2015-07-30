// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public interface Header
{
    String getName();
    
    String getValue();
    
    HeaderElement[] getElements() throws ParseException;
}
