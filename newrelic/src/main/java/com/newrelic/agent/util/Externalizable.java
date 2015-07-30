// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.io.IOException;
import java.io.DataOutputStream;

public interface Externalizable
{
    void write(DataOutputStream p0) throws IOException;
}
