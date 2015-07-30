// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.TransactionErrorPriority;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.AsyncRootTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.TransactionState;
import com.newrelic.agent.async.AsyncTransactionState;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class TransactionHolderDispatcherPointCut extends TracerFactoryPointCut
{
    public static final boolean DEFAULT_ENABLED = true;
    public static final String SCALA_INSTRUMENTATION_GROUP_NAME = "scala_instrumentation";
    public static final TransactionHolder TRANSACTION_HOLDER;
    private static final String POINT_CUT_NAME;
    
    public TransactionHolderDispatcherPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    public TransactionHolderDispatcherPointCut(final PointCutConfiguration config, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(config, classMatcher, methodMatcher);
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(TransactionHolderDispatcherPointCut.POINT_CUT_NAME, "scala_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return OrClassMatcher.getClassMatcher(new ExactClassMatcher("scala/concurrent/impl/Future$PromiseCompletingRunnable"), new ExactClassMatcher("scala/concurrent/impl/CallbackRunnable"));
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("run", "()V");
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, Object object, final Object[] args) {
        if (object instanceof ScalaPromiseCompletingRunnable) {
            object = ((ScalaPromiseCompletingRunnable)object)._nr_promise();
        }
        if (!(object instanceof TransactionHolder)) {
            Transaction.clearTransaction();
            return null;
        }
        final TransactionHolder txHolder = (TransactionHolder)object;
        final Object obj = txHolder._nr_getTransaction();
        Transaction savedTx = null;
        if (!(obj instanceof Transaction)) {
            return null;
        }
        savedTx = (Transaction)obj;
        if (tx == savedTx) {
            tx.getTransactionState().asyncJobInvalidate(txHolder);
            Agent.LOG.log(Level.FINEST, "The transaction is the same transaction as its parent. Transaction: {0}. Invalidating job {1}", new Object[] { tx, txHolder });
            return null;
        }
        if (tx.getDispatcher() != null) {
            Agent.LOG.log(Level.FINEST, "The job {0} is being run in an existing transaction {1}. Remove from parent transaction: {2}", new Object[] { txHolder, tx, savedTx });
            savedTx.getTransactionState().asyncJobInvalidate(txHolder);
            return null;
        }
        if (savedTx.getRootTransaction().isIgnore()) {
            return null;
        }
        tx.setTransactionState(new AsyncTransactionState(tx.getTransactionActivity(), savedTx.getInitialTransactionActivity()));
        tx.getTransactionState().asyncJobStarted(TransactionHolderDispatcherPointCut.TRANSACTION_HOLDER);
        tx.setRootTransaction(savedTx.getRootTransaction());
        savedTx.getTransactionState().asyncTransactionStarted(tx, txHolder);
        return this.createTracer(tx, sig, txHolder, savedTx);
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    private Tracer createTracer(final Transaction tx, final ClassMethodSignature sig, final TransactionHolder txHolder, final Transaction savedTx) {
        MetricNameFormat metricNameFormat = null;
        if (txHolder._nr_getName() == null) {
            metricNameFormat = new ClassMethodMetricNameFormat(sig, null);
        }
        else {
            metricNameFormat = new SimpleMetricNameFormat((String)txHolder._nr_getName());
        }
        return new AsyncRootTracer(tx, sig, txHolder, metricNameFormat) {
            public void finish(final int opcode, final Object returnValue) {
                super.finish(opcode, returnValue);
                if (sig.getClassName() != "scala/concurrent/impl/Future$PromiseCompletingRunnable") {
                    savedTx.getTransactionState().asyncJobFinished(txHolder);
                }
                final Throwable t = tx.getReportError();
                if (t != null) {
                    savedTx.getRootTransaction().setThrowable(t, TransactionErrorPriority.ASYNC_POINTCUT);
                }
                if (tx.isIgnore()) {
                    savedTx.setIgnore(true);
                }
                tx.getTransactionState().asyncJobFinished(TransactionHolderDispatcherPointCut.TRANSACTION_HOLDER);
            }
            
            public final void finish(final Throwable throwable) {
                super.finish(throwable);
                if (sig.getClassName() != "scala/concurrent/impl/Future$PromiseCompletingRunnable") {
                    savedTx.getTransactionState().asyncJobFinished(txHolder);
                }
                savedTx.getRootTransaction().setThrowable(throwable, TransactionErrorPriority.ASYNC_POINTCUT);
                if (tx.isIgnore()) {
                    savedTx.setIgnore(true);
                }
                tx.getTransactionState().asyncJobFinished(TransactionHolderDispatcherPointCut.TRANSACTION_HOLDER);
            }
        };
    }
    
    static {
        TRANSACTION_HOLDER = new TransactionHolder() {
            public Object _nr_getTransaction() {
                return null;
            }
            
            public void _nr_setTransaction(final Object tx) {
            }
            
            public Object _nr_getName() {
                return null;
            }
            
            public void _nr_setName(final Object tx) {
            }
        };
        POINT_CUT_NAME = TransactionHolderDispatcherPointCut.class.getName();
    }
    
    @InterfaceMixin(originalClassName = { "scala/concurrent/impl/Future$PromiseCompletingRunnable" })
    public interface ScalaPromiseCompletingRunnable
    {
        public static final String CLASS = "scala/concurrent/impl/Future$PromiseCompletingRunnable";
        
        @FieldAccessor(fieldName = "promise", fieldDesc = "Lscala/concurrent/impl/Promise$DefaultPromise;", existingField = true)
        Object _nr_promise();
    }
}
