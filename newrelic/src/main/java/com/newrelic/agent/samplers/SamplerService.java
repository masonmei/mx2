// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.service.Service;

public interface SamplerService extends Service
{
    Closeable addSampler(Runnable p0, long p1, TimeUnit p2);
}
