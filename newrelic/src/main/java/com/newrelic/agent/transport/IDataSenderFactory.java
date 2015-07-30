// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

import com.newrelic.agent.config.AgentConfig;

public interface IDataSenderFactory
{
    DataSender create(AgentConfig p0);
}
