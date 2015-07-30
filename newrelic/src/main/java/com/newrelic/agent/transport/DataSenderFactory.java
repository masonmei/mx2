// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

import com.newrelic.agent.config.AgentConfig;

public class DataSenderFactory
{
    private static volatile IDataSenderFactory DATA_SENDER_FACTORY;
    
    public static void setDataSenderFactory(final IDataSenderFactory dataSenderFactory) {
        if (dataSenderFactory == null) {
            return;
        }
        DataSenderFactory.DATA_SENDER_FACTORY = dataSenderFactory;
    }
    
    public static IDataSenderFactory getDataSenderFactory() {
        return DataSenderFactory.DATA_SENDER_FACTORY;
    }
    
    public static DataSender create(final AgentConfig config) {
        return DataSenderFactory.DATA_SENDER_FACTORY.create(config);
    }
    
    static {
        DataSenderFactory.DATA_SENDER_FACTORY = new DefaultDataSenderFactory();
    }
    
    private static class DefaultDataSenderFactory implements IDataSenderFactory
    {
        public DataSender create(final AgentConfig config) {
            return new DataSenderImpl(config);
        }
    }
}
