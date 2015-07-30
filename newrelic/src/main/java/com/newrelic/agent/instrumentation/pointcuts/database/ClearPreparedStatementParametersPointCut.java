// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ClearPreparedStatementParametersPointCut extends PointCut implements EntryInvocationHandler
{
    private static final String CLEAR_PARAMETERS_METHOD_NAME = "clearParameters";
    private static final MethodMatcher METHOD_MATCHER;
    
    public ClearPreparedStatementParametersPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("jdbc_parameterized_prepared_statement", null, ServiceFactory.getConfigService().getDefaultAgentConfig().isGenericJDBCSupportEnabled()), ParameterizedPreparedStatementPointCut.createClassMatcher(), ClearPreparedStatementParametersPointCut.METHOD_MATCHER);
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object statement, final Object[] args) {
        if (statement instanceof PreparedStatementExtension) {
            final PreparedStatementExtension preparedStatement = (PreparedStatementExtension)statement;
            final Object[] params = preparedStatement._nr_getSqlParameters();
            if (params != null) {
                for (int i = 0; i < params.length; ++i) {
                    params[i] = null;
                }
            }
        }
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    static {
        METHOD_MATCHER = new ExactMethodMatcher("clearParameters", "()V");
    }
}
