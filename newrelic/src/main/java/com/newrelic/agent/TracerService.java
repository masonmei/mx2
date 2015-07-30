// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.tracers.RetryException;
import java.util.List;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.util.Iterator;
import com.newrelic.agent.extension.ExtensionService;
import com.newrelic.agent.extension.ConfigurationConstruct;
import com.newrelic.agent.instrumentation.yaml.PointCutFactory;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.IgnoreTransactionTracerFactory;
import java.util.concurrent.ConcurrentHashMap;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.tracers.TracerFactory;
import java.util.Map;
import com.newrelic.agent.service.AbstractService;

public class TracerService extends AbstractService
{
    private final Map<String, TracerFactory> tracerFactories;
    private volatile PointCutInvocationHandler[] invocationHandlers;
    public ITracerService tracerServiceFactory;
    
    public TracerService() {
        super(TracerService.class.getSimpleName());
        this.tracerFactories = new ConcurrentHashMap<String, TracerFactory>();
        this.invocationHandlers = new PointCutInvocationHandler[0];
        this.registerTracerFactory(IgnoreTransactionTracerFactory.TRACER_FACTORY_NAME, new IgnoreTransactionTracerFactory());
        final ExtensionService extensionService = ServiceFactory.getExtensionService();
        for (final ConfigurationConstruct construct : PointCutFactory.getConstructs()) {
            extensionService.addConstruct(construct);
        }
        this.tracerServiceFactory = new NoOpTracerService();
    }
    
    public Tracer getTracer(final TracerFactory tracerFactory, final ClassMethodSignature signature, final Object object, final Object... args) {
        if (tracerFactory == null) {
            return null;
        }
        return this.tracerServiceFactory.getTracer(tracerFactory, signature, object, args);
    }
    
    public TracerFactory getTracerFactory(final String tracerFactoryName) {
        return this.tracerFactories.get(tracerFactoryName);
    }
    
    public void registerTracerFactory(final String name, final TracerFactory tracerFactory) {
        this.tracerFactories.put(name.intern(), tracerFactory);
    }
    
    public void registerInvocationHandlers(final List<PointCutInvocationHandler> handlers) {
        if (this.invocationHandlers == null) {
            this.invocationHandlers = handlers.toArray(new PointCutInvocationHandler[handlers.size()]);
        }
        else {
            final PointCutInvocationHandler[] arrayToSwap = new PointCutInvocationHandler[this.invocationHandlers.length + handlers.size()];
            System.arraycopy(this.invocationHandlers, 0, arrayToSwap, 0, this.invocationHandlers.length);
            System.arraycopy(handlers.toArray(), 0, arrayToSwap, this.invocationHandlers.length, handlers.size());
            this.invocationHandlers = arrayToSwap;
        }
    }
    
    public int getInvocationHandlerId(final PointCutInvocationHandler handler) {
        for (int i = 0; i < this.invocationHandlers.length; ++i) {
            if (this.invocationHandlers[i] == handler) {
                return i;
            }
        }
        return -1;
    }
    
    public PointCutInvocationHandler getInvocationHandler(final int id) {
        return this.invocationHandlers[id];
    }
    
    protected void doStart() {
    }
    
    protected void doStop() {
        this.tracerFactories.clear();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    private class NoOpTracerService implements ITracerService
    {
        public Tracer getTracer(final TracerFactory tracerFactory, final ClassMethodSignature signature, final Object object, final Object... args) {
            if (ServiceFactory.getServiceManager().isStarted()) {
                TracerService.this.tracerServiceFactory = new TracerServiceImpl();
                return TracerService.this.tracerServiceFactory.getTracer(tracerFactory, signature, object, args);
            }
            return null;
        }
    }
    
    private class TracerServiceImpl implements ITracerService
    {
        public Tracer getTracer(final TracerFactory tracerFactory, final ClassMethodSignature signature, final Object object, final Object... args) {
            final Transaction transaction = Transaction.getTransaction();
            if (transaction == null) {
                return null;
            }
            try {
                return transaction.getTransactionState().getTracer(transaction, tracerFactory, signature, object, args);
            }
            catch (RetryException e) {
                return this.getTracer(tracerFactory, signature, object, args);
            }
        }
    }
    
    private interface ITracerService
    {
        Tracer getTracer(TracerFactory p0, ClassMethodSignature p1, Object p2, Object... p3);
    }
}
