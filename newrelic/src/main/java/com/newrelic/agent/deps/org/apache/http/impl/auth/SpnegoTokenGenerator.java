// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.auth;

import java.io.IOException;

@Deprecated
public interface SpnegoTokenGenerator
{
    byte[] generateSpnegoDERObject(byte[] p0) throws IOException;
}
