// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.akka;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.TransactionErrorPriority;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.pointcuts.scala.TransactionHolderDispatcherPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class AkkaTransactionHolderDispatcherPointCut extends TransactionHolderDispatcherPointCut
{
    public static final boolean DEFAULT_ENABLED = true;
    public static final String AKKA_INSTRUMENTATION_GROUP_NAME = "akka_instrumentation";
    private static final String HANDLE_INVOKE_FAILURE_DESC = "(Lscala/collection/Iterable;Ljava/lang/Throwable;Ljava/lang/String;)V";
    private static final String HANDLE_INVOKE_FAILURE_METHOD = "handleInvokeFailure";
    private static final String POINT_CUT_NAME;
    
    public AkkaTransactionHolderDispatcherPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(AkkaTransactionHolderDispatcherPointCut.POINT_CUT_NAME, "akka_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return OrClassMatcher.getClassMatcher(new ExactClassMatcher("akka/actor/ActorCell"), new ExactClassMatcher("akka/dispatch/Future$$anon$3"), new ExactClassMatcher("akka/dispatch/Future$$anon$4"));
    }
    
    private static MethodMatcher createMethodMatcher() {
        return OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("run", "()V"), new NameMethodMatcher("invoke"), new ExactMethodMatcher("handleInvokeFailure", "(Lscala/collection/Iterable;Ljava/lang/Throwable;Ljava/lang/String;)V"));
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, Object object, final Object[] args) {
        if (object instanceof ActorCell) {
            if (sig.getMethodName() == "handleInvokeFailure") {
                final Throwable t = (Throwable)args[1];
                tx.getRootTransaction().setThrowable(t, TransactionErrorPriority.ASYNC_POINTCUT);
                return null;
            }
            Object currentMessage = args[0];
            if (!(currentMessage instanceof TransactionHolder)) {
                final ActorCell actorCell = (ActorCell)object;
                currentMessage = actorCell._nr_currentMessage();
            }
            if (currentMessage instanceof TransactionHolder) {
                return super.doGetTracer(tx, sig, currentMessage, null);
            }
        }
        if (object instanceof AkkaPromiseCompletingRunnable) {
            object = ((AkkaPromiseCompletingRunnable)object)._nr_promise();
        }
        if (object instanceof TransactionHolder) {
            return super.doGetTracer(tx, sig, object, args);
        }
        Transaction.clearTransaction();
        return null;
    }
    
    static {
        POINT_CUT_NAME = AkkaTransactionHolderDispatcherPointCut.class.getName();
    }
    
    @InterfaceMixin(originalClassName = { "akka/dispatch/Future$$anon$3" })
    public interface AkkaPromiseCompletingRunnable
    {
        public static final String CLASS = "akka/dispatch/Future$$anon$3";
        
        @FieldAccessor(fieldName = "promise$1", fieldDesc = "Lakka/dispatch/Promise;", existingField = true)
        Object _nr_promise();
    }
    
    @InterfaceMixin(originalClassName = { "akka/actor/ActorCell" })
    public interface ActorCell
    {
        public static final String CLASS = "akka/actor/ActorCell";
        
        @FieldAccessor(fieldName = "currentMessage", fieldDesc = "Lakka/dispatch/Envelope;", existingField = true)
        Object _nr_currentMessage();
    }
}
