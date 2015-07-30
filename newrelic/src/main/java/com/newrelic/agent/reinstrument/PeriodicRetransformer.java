// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.reinstrument;

import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.lang.instrument.UnmodifiableClassException;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PeriodicRetransformer implements Runnable
{
    private static final int FREQUENCY_IN_SECONDS = 10;
    private final AtomicReference<ConcurrentLinkedQueue<Class<?>>> classesToRetransform;
    private final AtomicBoolean scheduled;
    public static final PeriodicRetransformer INSTANCE;
    
    private PeriodicRetransformer() {
        this.classesToRetransform = new AtomicReference<ConcurrentLinkedQueue<Class<?>>>(new ConcurrentLinkedQueue<Class<?>>());
        this.scheduled = new AtomicBoolean(false);
    }
    
    public void run() {
        final ConcurrentLinkedQueue<Class<?>> classList = this.classesToRetransform.getAndSet(new ConcurrentLinkedQueue<Class<?>>());
        if (classList.isEmpty()) {
            return;
        }
        final Set<Class<?>> classSet = (Set<Class<?>>)Sets.newHashSet((Iterable<?>)classList);
        try {
            ServiceFactory.getAgent().getInstrumentation().retransformClasses((Class<?>[])classSet.toArray(new Class[0]));
        }
        catch (UnmodifiableClassException e) {
            Agent.LOG.fine(MessageFormat.format("Unable to retransform class: {0}", e.getMessage()));
        }
    }
    
    public void queueRetransform(final Class<?> classToRetransform) {
        this.classesToRetransform.get().add(classToRetransform);
        if (!this.scheduled.get() && !this.scheduled.getAndSet(true)) {
            ServiceFactory.getSamplerService().addSampler(this, 10L, TimeUnit.SECONDS);
        }
    }
    
    static {
        INSTANCE = new PeriodicRetransformer();
    }
}
