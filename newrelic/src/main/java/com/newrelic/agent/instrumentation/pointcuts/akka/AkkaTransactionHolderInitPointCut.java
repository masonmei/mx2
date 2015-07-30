// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.akka;

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
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class AkkaTransactionHolderInitPointCut extends PointCut implements EntryInvocationHandler
{
    public static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    
    public AkkaTransactionHolderInitPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(AkkaTransactionHolderInitPointCut.POINT_CUT_NAME, "akka_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return OrClassMatcher.getClassMatcher(new ExactClassMatcher("akka/dispatch/Envelope"), new ExactClassMatcher("akka/dispatch/AbstractPromise"), new ExactClassMatcher("akka/dispatch/Future$$anon$4"));
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new NameMethodMatcher("<init>");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (object instanceof TransactionHolder) {
            final TransactionHolder th = (TransactionHolder)object;
            final Transaction tx = Transaction.getTransaction();
            if (tx.isStarted() && th._nr_getTransaction() == null) {
                if (!(tx.getTransactionState() instanceof AsyncTransactionState)) {
                    return;
                }
                th._nr_setTransaction(tx);
                if (args.length > 1) {
                    String akkaOrigin = args[1].toString();
                    akkaOrigin = akkaOrigin.replaceAll("\\$[^/\\]]+", "");
                    akkaOrigin = akkaOrigin.replace("/", "\\");
                    akkaOrigin = akkaOrigin.replace(".", "_");
                    th._nr_setName(MessageFormat.format("Java/{0}/tell", akkaOrigin));
                }
                tx.getTransactionState().asyncJobStarted(th);
            }
        }
    }
    
    static {
        POINT_CUT_NAME = AkkaTransactionHolderInitPointCut.class.getName();
    }
}
