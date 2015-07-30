// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.ssl;

import java.net.Socket;
import java.util.Map;

public interface PrivateKeyStrategy
{
    String chooseAlias(Map<String, PrivateKeyDetails> p0, Socket p1);
}
