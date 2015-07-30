// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.entity;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.HttpMessage;

public interface ContentLengthStrategy
{
    public static final int IDENTITY = -1;
    public static final int CHUNKED = -2;
    
    long determineLength(HttpMessage p0) throws HttpException;
}
