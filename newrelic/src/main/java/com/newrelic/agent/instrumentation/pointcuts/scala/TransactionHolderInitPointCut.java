// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import com.newrelic.agent.async.AsyncTransactionState;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class TransactionHolderInitPointCut extends PointCut implements EntryInvocationHandler
{
    public static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    private final boolean tracerNamingEnabled;
    
    public TransactionHolderInitPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
        this.tracerNamingEnabled = ServiceFactory.getConfigService().getTransactionTracerConfig(null).isStackBasedNamingEnabled();
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(TransactionHolderInitPointCut.POINT_CUT_NAME, "scala_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return OrClassMatcher.getClassMatcher(new ExactClassMatcher("scala/concurrent/impl/AbstractPromise"), new ExactClassMatcher("scala/concurrent/impl/CallbackRunnable"));
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new NameMethodMatcher("<init>");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (object instanceof TransactionHolder) {
            final Transaction tx = Transaction.getTransaction();
            final TransactionHolder th = (TransactionHolder)object;
            if (tx.isStarted() && th._nr_getTransaction() == null) {
                if (!(tx.getTransactionState() instanceof AsyncTransactionState) || tx.getRootTransaction().isIgnore()) {
                    return;
                }
                th._nr_setTransaction(tx);
                if (this.tracerNamingEnabled) {
                    th._nr_setName(this.findTxName(th));
                }
                tx.getTransactionState().asyncJobStarted(th);
            }
        }
    }
    
    private String findTxName(final TransactionHolder th) {
        if (th instanceof CallbackRunnable) {
            final Object onComplete = ((CallbackRunnable)th).onComplete();
            return this.analyzeOnComplete(onComplete);
        }
        for (final StackTraceElement st : Thread.currentThread().getStackTrace()) {
            if (!st.getClassName().startsWith("com.newrelic.agent.")) {
                if (!st.getClassName().startsWith("com.newrelic.bootstrap.")) {
                    if (!st.getClassName().startsWith("com.newrelic.api.agent.")) {
                        if (!st.getMethodName().equals("<init>")) {
                            if (!st.getClassName().startsWith("scala.concurrent")) {
                                if (!st.getClassName().startsWith("scala.collection")) {
                                    if (!st.getClassName().startsWith("play.api.libs.concurrent")) {
                                        if (!st.getClassName().startsWith("play.api.libs.iteratee")) {
                                            if (!st.getClassName().startsWith("play.libs.F")) {
                                                if (!st.getClassName().startsWith("akka.pattern.PromiseActorRef")) {
                                                    if (!st.getClassName().startsWith("java.util.concurrent.ThreadPoolExecutor")) {
                                                        if (!st.getClassName().startsWith("java.lang.Thread")) {
                                                            return MessageFormat.format("Java/{0}/{1}", st.getClassName(), st.getMethodName());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private String analyzeOnComplete(final Object onComplete) {
        for (final Field field : onComplete.getClass().getDeclaredFields()) {
            if (field.getType().getName().startsWith("scala.Function")) {
                final boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    final Object callback = field.get(onComplete);
                    if (callback.getClass().getName().startsWith("scala.concurrent") || callback.getClass().getName().startsWith("play.api.libs.concurrent") || callback.getClass().getName().startsWith("play.api.libs.iteratee")) {
                        final String analyzeOnComplete = this.analyzeOnComplete(callback);
                        field.setAccessible(accessible);
                        return analyzeOnComplete;
                    }
                    final String format = MessageFormat.format("Java/{0}/apply", callback.getClass().getName());
                    field.setAccessible(accessible);
                    return format;
                }
                catch (Exception e) {
                    final String s = null;
                    field.setAccessible(accessible);
                    return s;
                }
                finally {
                    field.setAccessible(accessible);
                }
            }
        }
        return MessageFormat.format("Java/{0}/apply", onComplete.getClass().getName());
    }
    
    static {
        POINT_CUT_NAME = TransactionHolderInitPointCut.class.getName();
    }
    
    @InterfaceMixin(originalClassName = { "scala/concurrent/impl/CallbackRunnable" })
    public interface CallbackRunnable
    {
        @FieldAccessor(fieldName = "onComplete", fieldDesc = "Lscala/Function1;", existingField = true)
        Object onComplete();
    }
}
