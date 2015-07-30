// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.io.Closeable;
import com.newrelic.agent.bridge.NoOpTransaction;
import com.newrelic.agent.TransactionApiImpl;
import com.newrelic.agent.instrumentation.weaver.WeaveInstrumentation;
import java.lang.instrument.UnmodifiableClassException;
import com.newrelic.agent.reinstrument.PeriodicRetransformer;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import java.lang.reflect.Modifier;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import com.newrelic.agent.instrumentation.classmatchers.DefaultClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.HashSafeClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.AndMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.NotMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.GetterSetterMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.AccessMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.metricname.MetricNameFormats;
import com.newrelic.agent.tracers.TracerFlags;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.util.logging.Level;
import com.newrelic.agent.tracers.ClassMethodSignatures;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.bridge.ExitTracer;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.Set;
import com.newrelic.agent.util.InsertOnlyArray;
import com.newrelic.api.agent.Logger;
import com.newrelic.agent.bridge.Instrumentation;

public class InstrumentationImpl implements Instrumentation
{
    private final Logger logger;
    private final InsertOnlyArray<Object> objectCache;
    private final Set<Type> weaveClasses;
    
    public InstrumentationImpl(final Logger logger) {
        this.objectCache = new InsertOnlyArray<Object>(16);
        this.weaveClasses = Sets.newSetFromMap((Map<Type, Boolean>)Maps.newConcurrentMap());
        this.logger = logger;
    }
    
    public ExitTracer createTracer(final Object invocationTarget, final int signatureId, final boolean dispatcher, final String metricName, final String tracerFactoryName, final Object[] args) {
        final Transaction transaction = Transaction.getTransaction();
        if (transaction == null) {
            return null;
        }
        if (ServiceFactory.getServiceManager().getCircuitBreakerService().isTripped()) {
            return null;
        }
        try {
            if (!dispatcher && !transaction.isStarted() && tracerFactoryName == null) {
                return null;
            }
            if (transaction.getTransactionActivity().isFlyweight()) {
                return null;
            }
            final ClassMethodSignature sig = ClassMethodSignatures.get().get(signatureId);
            return (ExitTracer)transaction.getTransactionState().getTracer(transaction, tracerFactoryName, sig, invocationTarget, args);
        }
        catch (Throwable t) {
            this.logger.log(Level.FINEST, t, "createTracer({0}, {1}, {2})", new Object[] { invocationTarget, signatureId, metricName });
            return null;
        }
    }
    
    public ExitTracer createTracer(final Object invocationTarget, final int signatureId, final String metricName, final int flags) {
        try {
            if (ServiceFactory.getServiceManager().getCircuitBreakerService().isTripped()) {
                return null;
            }
            if (!Agent.canFastPath()) {
                return this.oldCreateTracer(invocationTarget, signatureId, metricName, flags);
            }
            final TransactionActivity txa = TransactionActivity.get();
            if (txa == null) {
                if (!TracerFlags.isDispatcher(flags)) {
                    return null;
                }
                final Transaction tx = Transaction.getTransaction();
                final ClassMethodSignature sig = ClassMethodSignatures.get().get(signatureId);
                return (ExitTracer)tx.getTransactionState().getTracer(tx, invocationTarget, sig, metricName, flags);
            }
            else {
                if (!TracerFlags.isDispatcher(flags) && !txa.isStarted()) {
                    return null;
                }
                Tracer result = null;
                if (txa.checkTracerStart()) {
                    try {
                        final ClassMethodSignature sig = ClassMethodSignatures.get().get(signatureId);
                        final MetricNameFormat mnf = MetricNameFormats.getFormatter(invocationTarget, sig, metricName, flags);
                        if (TracerFlags.isDispatcher(flags)) {
                            result = new OtherRootTracer(txa, sig, invocationTarget, mnf);
                        }
                        else {
                            result = new DefaultTracer(txa, sig, invocationTarget, mnf, flags);
                        }
                        txa.unlockTracerStart();
                    }
                    finally {
                        txa.unlockTracerStart();
                    }
                    txa.tracerStarted(result);
                }
                return (ExitTracer)result;
            }
        }
        catch (Throwable t) {
            this.logger.log(Level.FINEST, t, "createTracer({0}, {1}, {2}, {3})", new Object[] { invocationTarget, signatureId, metricName, flags });
            return null;
        }
    }
    
    private ExitTracer oldCreateTracer(final Object invocationTarget, final int signatureId, final String metricName, final int flags) {
        final Transaction transaction = Transaction.getTransaction();
        if (transaction == null) {
            return null;
        }
        try {
            if (!TracerFlags.isDispatcher(flags) && !transaction.isStarted()) {
                return null;
            }
            if (transaction.getTransactionActivity().isFlyweight()) {
                return null;
            }
            final ClassMethodSignature sig = ClassMethodSignatures.get().get(signatureId);
            return (ExitTracer)transaction.getTransactionState().getTracer(transaction, invocationTarget, sig, metricName, flags);
        }
        catch (Throwable t) {
            this.logger.log(Level.FINEST, t, "createTracer({0}, {1}, {2}, {3})", new Object[] { invocationTarget, signatureId, metricName, flags });
            return null;
        }
    }
    
    public void noticeInstrumentationError(final Throwable throwable, final String libraryName) {
        if (Agent.LOG.isFinerEnabled()) {
            this.logger.log(Level.FINER, "An error was thrown from instrumentation library ", new Object[] { libraryName });
            this.logger.log(Level.FINEST, throwable, "An error was thrown from instrumentation library ", new Object[] { libraryName });
        }
    }
    
    public void instrument(final String className, final String metricPrefix) {
        final DefaultClassAndMethodMatcher matcher = new HashSafeClassAndMethodMatcher(new ExactClassMatcher(className), AndMethodMatcher.getMethodMatcher(new AccessMethodMatcher(1), new NotMethodMatcher(GetterSetterMethodMatcher.getGetterSetterMethodMatcher())));
        ServiceFactory.getClassTransformerService().addTraceMatcher(matcher, metricPrefix);
    }
    
    public void instrument(final Method methodToInstrument, final String metricPrefix) {
        if (methodToInstrument.isAnnotationPresent((Class<? extends Annotation>)InstrumentedMethod.class)) {
            return;
        }
        if (OptimizedClassMatcher.METHODS_WE_NEVER_INSTRUMENT.contains(com.newrelic.agent.deps.org.objectweb.asm.commons.Method.getMethod(methodToInstrument))) {
            return;
        }
        final int modifiers = methodToInstrument.getModifiers();
        if (Modifier.isNative(modifiers) || Modifier.isAbstract(modifiers)) {
            return;
        }
        final Class<?> declaringClass = methodToInstrument.getDeclaringClass();
        final DefaultClassAndMethodMatcher matcher = new HashSafeClassAndMethodMatcher(new ExactClassMatcher(declaringClass.getName()), new ExactMethodMatcher(methodToInstrument.getName(), Type.getMethodDescriptor(methodToInstrument)));
        final boolean shouldRetransform = ServiceFactory.getClassTransformerService().addTraceMatcher(matcher, metricPrefix);
        if (shouldRetransform) {
            this.logger.log(Level.FINE, "Retransforming {0} for instrumentation.", new Object[] { methodToInstrument });
            PeriodicRetransformer.INSTANCE.queueRetransform(declaringClass);
        }
    }
    
    public void retransformUninstrumentedClass(final Class<?> classToRetransform) {
        if (!classToRetransform.isAnnotationPresent((Class<? extends Annotation>)InstrumentedClass.class)) {
            this.retransformClass(classToRetransform);
        }
        else {
            this.logger.log(Level.FINER, "Class ", new Object[] { classToRetransform, " already instrumented." });
        }
    }
    
    private void retransformClass(final Class<?> classToRetransform) {
        try {
            ServiceFactory.getAgent().getInstrumentation().retransformClasses(classToRetransform);
        }
        catch (UnmodifiableClassException e) {
            this.logger.log(Level.FINE, "Unable to retransform class ", new Object[] { classToRetransform, " : ", e.getMessage() });
        }
    }
    
    public Class<?> loadClass(final ClassLoader classLoader, final Class<?> theClass) throws ClassNotFoundException {
        this.logger.log(Level.FINE, "Loading class ", new Object[] { theClass.getName(), " using class loader ", classLoader.toString() });
        try {
            return classLoader.loadClass(theClass.getName());
        }
        catch (ClassNotFoundException e) {
            this.logger.log(Level.FINEST, "Unable to load", new Object[] { theClass.getName(), ".  Appending it to the classloader." });
            final WeaveInstrumentation weaveInstrumentation = theClass.getAnnotation(WeaveInstrumentation.class);
            if (weaveInstrumentation != null) {
                this.logger.log(Level.FINE, theClass.getName(), new Object[] { " is defined in ", weaveInstrumentation.title(), " version ", weaveInstrumentation.version() });
                try {
                    ServiceFactory.getClassTransformerService().getContextManager().getClassWeaverService().loadClass(classLoader, weaveInstrumentation.title(), theClass.getName());
                    return classLoader.loadClass(theClass.getName());
                }
                catch (Exception e2) {
                    throw new ClassNotFoundException("Unable to load " + theClass.getName() + " from instrumentation package " + weaveInstrumentation.title());
                }
            }
            throw new ClassNotFoundException("Unable to load " + theClass.getName());
        }
    }
    
    public com.newrelic.agent.bridge.Transaction getTransaction() {
        try {
            final Transaction innerTx = Transaction.getTransaction();
            if (innerTx != null) {
                return (com.newrelic.agent.bridge.Transaction)new TransactionApiImpl();
            }
        }
        catch (Throwable t) {
            this.logger.log(Level.FINE, t, "Unable to get transaction, using no-op transaction instead", new Object[0]);
        }
        return NoOpTransaction.INSTANCE;
    }
    
    public int addToObjectCache(final Object object) {
        return this.objectCache.add(object);
    }
    
    public Object getCachedObject(final int id) {
        return this.objectCache.get(id);
    }
    
    public boolean isWeaveClass(final Class<?> clazz) {
        return this.weaveClasses.contains(Type.getType(clazz));
    }
    
    public void addWeaveClass(final Type type) {
        this.weaveClasses.add(type);
    }
    
    public void registerCloseable(final String instrumentationName, final Closeable closeable) {
        if (instrumentationName != null && closeable != null) {
            ServiceFactory.getClassTransformerService().getContextManager().getClassWeaverService().registerInstrumentationCloseable(instrumentationName, closeable);
        }
    }
}
