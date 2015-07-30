// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.reinstrument;

import com.newrelic.agent.service.Service;

public interface RemoteInstrumentationService extends Service
{
    ReinstrumentResult processXml(String p0);
}
