// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import java.util.Iterator;
import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.Instrumentation;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.bridge.PublicApi;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.logging.Level;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.util.asm.BytecodeGenProxyBuilder;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Collection;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import com.newrelic.agent.instrumentation.context.TraceInformation;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class TraceClassVisitor extends ClassVisitor
{
    private final String className;
    private final InstrumentationContext instrumentationContext;
    private final TraceInformation traceInfo;
    private final Set<Method> tracedMethods;
    
    public TraceClassVisitor(final ClassVisitor cv, final String className, final InstrumentationContext context) {
        super(327680, cv);
        this.tracedMethods = (Set<Method>)Sets.newHashSet();
        this.className = className;
        this.instrumentationContext = context;
        this.traceInfo = context.getTraceInformation();
    }
    
    public void visitEnd() {
        super.visitEnd();
        if (!this.traceInfo.getTraceAnnotations().isEmpty()) {
            Agent.LOG.finer("Traced " + this.className + " methods " + this.tracedMethods);
            if (this.tracedMethods.size() != this.traceInfo.getTraceAnnotations().size()) {
                final Set<Method> expected = (Set<Method>)Sets.newHashSet((Iterable<?>)this.traceInfo.getTraceAnnotations().keySet());
                expected.removeAll(this.tracedMethods);
                Agent.LOG.finer("While tracing " + this.className + " the following methods were not traced: " + expected);
            }
        }
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        final Method method = new Method(name, desc);
        if (this.traceInfo.getIgnoreTransactionMethods().contains(method)) {
            this.instrumentationContext.markAsModified();
            return new AdviceAdapter(327680, mv, access, name, desc) {
                protected void onMethodEnter() {
                    BridgeUtils.getCurrentTransaction(this);
                    BytecodeGenProxyBuilder.newBuilder(Transaction.class, this, true).build().ignore();
                }
            };
        }
        final TraceDetails trace = this.traceInfo.getTraceAnnotations().get(method);
        if (null != trace) {
            this.tracedMethods.add(method);
            final PointCut pointCut = this.instrumentationContext.getOldStylePointCut(method);
            if (pointCut == null) {
                final boolean custom = trace.isCustom();
                if (trace.excludeFromTransactionTrace() && trace.isLeaf()) {
                    mv = new FlyweightTraceMethodVisitor(this.className, mv, access, name, desc, trace, this.instrumentationContext.getClassBeingRedefined());
                }
                else {
                    mv = new TraceMethodVisitor(this.className, mv, access, name, desc, trace, custom, this.instrumentationContext.getClassBeingRedefined());
                    if (!trace.getParameterAttributeNames().isEmpty()) {
                        for (final ParameterAttributeName param : trace.getParameterAttributeNames()) {
                            final ParameterAttributeName attr = param;
                            if (param.getMethodMatcher().matches(access, name, desc, null)) {
                                try {
                                    final Type type = method.getArgumentTypes()[param.getIndex()];
                                    if (type.getSort() == 9) {
                                        Agent.LOG.log(Level.FINE, "Unable to record an attribute value for {0}.{1} because it is an array", new Object[] { this.className, method });
                                    }
                                    else {
                                        mv = new AdviceAdapter(262144, mv, access, name, desc) {
                                            protected void onMethodEnter() {
                                                super.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "publicApi", BridgeUtils.PUBLIC_API_TYPE);
                                                final PublicApi api = BytecodeGenProxyBuilder.newBuilder(PublicApi.class, this, false).build();
                                                this.push(param.getAttributeName());
                                                this.loadArg(param.getIndex());
                                                if (type.getSort() != 10) {
                                                    this.box(type);
                                                }
                                                this.dup();
                                                this.instanceOf(Type.getType(Number.class));
                                                final Label objectLabel = this.newLabel();
                                                final Label skipLabel = this.newLabel();
                                                this.ifZCmp(153, objectLabel);
                                                this.checkCast(Type.getType(Number.class));
                                                api.addCustomParameter("", (Number)0);
                                                this.goTo(skipLabel);
                                                this.visitLabel(objectLabel);
                                                this.invokeVirtual(Type.getType(Object.class), new Method("toString", Type.getType(String.class), new Type[0]));
                                                api.addCustomParameter("", "");
                                                this.visitLabel(skipLabel);
                                            }
                                        };
                                    }
                                }
                                catch (ArrayIndexOutOfBoundsException e) {
                                    Agent.LOG.log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
                                }
                            }
                        }
                    }
                    if (trace.rollupMetricName().length > 0) {
                        final int cacheId = AgentBridge.instrumentation.addToObjectCache((Object)trace.rollupMetricName());
                        mv = new AdviceAdapter(327680, mv, access, name, desc) {
                            protected void onMethodEnter() {
                                this.getStatic(BridgeUtils.TRACED_METHOD_TYPE, "CURRENT", BridgeUtils.TRACED_METHOD_TYPE);
                                super.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "instrumentation", BridgeUtils.INSTRUMENTATION_TYPE);
                                final Instrumentation instrumentation = BytecodeGenProxyBuilder.newBuilder(Instrumentation.class, this, true).build();
                                instrumentation.getCachedObject(cacheId);
                                super.checkCast(Type.getType(String[].class));
                                final TracedMethod tracedMethod = BytecodeGenProxyBuilder.newBuilder(TracedMethod.class, this, false).build();
                                tracedMethod.setRollupMetricNames((String[])null);
                            }
                        };
                    }
                    if (TransactionName.isSimpleTransactionName(trace.transactionName())) {
                        mv = new AdviceAdapter(327680, mv, access, name, desc) {
                            protected void onMethodEnter() {
                                final TracedMethod tracedMethod = BytecodeGenProxyBuilder.newBuilder(TracedMethod.class, this, true).build();
                                this.getStatic(BridgeUtils.TRACED_METHOD_TYPE, "CURRENT", BridgeUtils.TRACED_METHOD_TYPE);
                                tracedMethod.nameTransaction(trace.transactionName().transactionNamePriority);
                            }
                        };
                    }
                    else if (trace.transactionName() != null) {
                        mv = new AdviceAdapter(327680, mv, access, name, desc) {
                            protected void onMethodEnter() {
                                BridgeUtils.getCurrentTransaction(this);
                                final Transaction transaction = BytecodeGenProxyBuilder.newBuilder(Transaction.class, this, true).build();
                                final TransactionName transactionName = trace.transactionName();
                                transaction.setTransactionName(transactionName.transactionNamePriority, transactionName.override, transactionName.category, new String[] { transactionName.path });
                                this.pop();
                            }
                        };
                    }
                    if (trace.isWebTransaction()) {
                        mv = new AdviceAdapter(327680, mv, access, name, desc) {
                            protected void onMethodExit(final int opcode) {
                                this.getStatic(BridgeUtils.TRANSACTION_TYPE, "CURRENT", BridgeUtils.TRANSACTION_TYPE);
                                BytecodeGenProxyBuilder.newBuilder(Transaction.class, this, true).build().convertToWebTransaction();
                            }
                        };
                    }
                }
                this.instrumentationContext.addTimedMethods(method);
            }
            else {
                Agent.LOG.warning(this.className + '.' + method + " is matched to trace, but it was already instrumented by " + pointCut.toString());
            }
        }
        if (this.traceInfo.getIgnoreApdexMethods().contains(method)) {
            this.instrumentationContext.markAsModified();
            mv = new AdviceAdapter(327680, mv, access, name, desc) {
                protected void onMethodEnter() {
                    this.invokeStatic(BridgeUtils.NEW_RELIC_API_TYPE, TraceMethodVisitor.IGNORE_APDEX_METHOD);
                }
            };
        }
        return mv;
    }
}
