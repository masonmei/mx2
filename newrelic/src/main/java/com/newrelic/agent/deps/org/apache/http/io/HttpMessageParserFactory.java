// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.io;

import com.newrelic.agent.deps.org.apache.http.config.MessageConstraints;
import com.newrelic.agent.deps.org.apache.http.HttpMessage;

public interface HttpMessageParserFactory<T extends HttpMessage>
{
    HttpMessageParser<T> create(SessionInputBuffer p0, MessageConstraints p1);
}
