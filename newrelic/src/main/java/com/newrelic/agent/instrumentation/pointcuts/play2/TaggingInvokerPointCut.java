// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play2;

import com.newrelic.agent.bridge.TransactionNamePriority;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import java.text.MessageFormat;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class TaggingInvokerPointCut extends PointCut implements EntryInvocationHandler
{
    private static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    public static final String CLASS_NAME = "play/core/Router$Routes$TaggingInvoker";
    private static final String METHOD_NAME = "call";
    private static final String METHOD_DESC = "(Lscala/Function0;)Lplay/api/mvc/Handler;";
    
    public TaggingInvokerPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(TaggingInvokerPointCut.POINT_CUT_NAME, "play2_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("play/core/Router$Routes$TaggingInvoker");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("call", "(Lscala/Function0;)Lplay/api/mvc/Handler;");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Transaction tx = Transaction.getTransaction();
        if (!tx.isStarted()) {
            return;
        }
        if (object instanceof TaggingInvoker) {
            final TaggingInvoker taggingInvoker = (TaggingInvoker)object;
            final Object handlerDef = taggingInvoker.getHandlerDef();
            if (handlerDef instanceof HandlerDef) {
                this.setTransactionName(tx, (HandlerDef)handlerDef);
            }
        }
    }
    
    private void setTransactionName(final Transaction tx, final HandlerDef handlerDef) {
        final String action = MessageFormat.format("{0}.{1}", handlerDef.controller(), handlerDef.method());
        this.setTransactionName(tx, action);
    }
    
    private void setTransactionName(final Transaction tx, final String controllerAction) {
        if (!tx.isTransactionNamingEnabled()) {
            return;
        }
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(tx, TransactionNamePriority.FRAMEWORK_LOW)) {
            final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Play 2.3 controller action", controllerAction);
            Agent.LOG.finer(msg);
        }
        policy.setTransactionName(tx, controllerAction, "PlayControllerAction", TransactionNamePriority.FRAMEWORK_LOW);
    }
    
    static {
        POINT_CUT_NAME = TaggingInvokerPointCut.class.getName();
    }
}
