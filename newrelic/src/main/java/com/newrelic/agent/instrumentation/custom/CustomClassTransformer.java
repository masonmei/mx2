// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.custom;

import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.lang.instrument.IllegalClassFormatException;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.security.ProtectionDomain;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import java.util.List;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;

public class CustomClassTransformer implements ContextClassTransformer
{
    final List<ExtensionClassAndMethodMatcher> extensionPointCuts;
    private final InstrumentationContextManager contextManager;
    private final ClassMatchVisitorFactory matcher;
    
    public CustomClassTransformer(final InstrumentationContextManager contextManager, final List<ExtensionClassAndMethodMatcher> extensionPointCuts) {
        this.extensionPointCuts = extensionPointCuts;
        contextManager.addContextClassTransformer(this.matcher = OptimizedClassMatcherBuilder.newBuilder().addClassMethodMatcher((ClassAndMethodMatcher[])extensionPointCuts.toArray(new ExtensionClassAndMethodMatcher[0])).build(), this);
        this.contextManager = contextManager;
    }
    
    public void destroy() {
        this.contextManager.removeMatchVisitor(this.matcher);
    }
    
    public ClassMatchVisitorFactory getMatcher() {
        return this.matcher;
    }
    
    public byte[] transform(final ClassLoader pLoader, final String pClassName, final Class<?> pClassBeingRedefined, final ProtectionDomain pProtectionDomain, final byte[] pClassfileBuffer, final InstrumentationContext pContext, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
        try {
            this.addMatchesToTraces(pContext, match);
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINE, MessageFormat.format("Unable to transform class {0}", pClassName));
            if (Agent.LOG.isFinestEnabled()) {
                Agent.LOG.log(Level.FINEST, MessageFormat.format("Unable to transform class {0}", pClassName), t);
            }
        }
        return null;
    }
    
    private void addMatchesToTraces(final InstrumentationContext pContext, final OptimizedClassMatcher.Match match) {
        final Collection<ExtensionClassAndMethodMatcher> matches = (Collection<ExtensionClassAndMethodMatcher>)Lists.newArrayList((Iterable<?>)this.extensionPointCuts);
        matches.retainAll(match.getClassMatches().keySet());
        if (!matches.isEmpty()) {
            for (final ExtensionClassAndMethodMatcher pc : matches) {
                for (Method m : match.getMethods()) {
                    if (pc.getMethodMatcher().matches(-1, m.getName(), m.getDescriptor(), match.getMethodAnnotations(m))) {
                        final Method method = pContext.getBridgeMethods().get(m);
                        if (method != null) {
                            m = method;
                        }
                        final TraceDetails td = pc.getTraceDetails();
                        if (td.ignoreTransaction()) {
                            if (Agent.LOG.isFinerEnabled()) {
                                Agent.LOG.log(Level.FINER, MessageFormat.format("Matched method {0} for ignoring the transaction trace.", m.toString()));
                            }
                            pContext.addIgnoreTransactionMethod(m);
                        }
                        else {
                            if (Agent.LOG.isFinerEnabled()) {
                                Agent.LOG.log(Level.FINER, MessageFormat.format("Matched method {0} for instrumentation.", m.toString()));
                            }
                            pContext.addTrace(m, td);
                        }
                    }
                }
            }
        }
    }
}
