// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.service.NoopService;

public class NoopSamplerService extends NoopService implements SamplerService
{
    public NoopSamplerService() {
        super("SamplerService");
    }
    
    public Closeable addSampler(final Runnable sampler, final long period, final TimeUnit timeUnit) {
        return null;
    }
}
