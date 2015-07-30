// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.util.Set;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.JDBCClassTransformer;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ParameterizedPreparedStatementPointCut extends PointCut implements EntryInvocationHandler
{
    static final String NAME = "jdbc_parameterized_prepared_statement";
    private static final String SET_INT_METHOD_NAME = "setInt";
    private static final String SET_NULL_METHOD_NAME = "setNull";
    private static final String SET_BOOLEAN_METHOD_NAME = "setBoolean";
    private static final String SET_BYTE_METHOD_NAME = "setByte";
    private static final String SET_SHORT_METHOD_NAME = "setShort";
    private static final String SET_LONG_METHOD_NAME = "setLong";
    private static final String SET_FLOAT_METHOD_NAME = "setFloat";
    private static final String SET_DOUBLE_METHOD_NAME = "setDouble";
    private static final String SET_BIG_DECIMAL_METHOD_NAME = "setBigDecimal";
    private static final String SET_STRING_METHOD_NAME = "setString";
    private static final String SET_DATE_METHOD_NAME = "setDate";
    private static final String SET_TIME_METHOD_NAME = "setTime";
    private static final String SET_TIMESTAMP_METHOD_NAME = "setTimestamp";
    private static final MethodMatcher METHOD_MATCHER;
    private final IAgentLogger logger;
    
    public ParameterizedPreparedStatementPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("jdbc_parameterized_prepared_statement", null, ServiceFactory.getConfigService().getDefaultAgentConfig().isGenericJDBCSupportEnabled()), createClassMatcher(), ParameterizedPreparedStatementPointCut.METHOD_MATCHER);
        this.logger = Agent.LOG.getChildLogger(this.getClass());
    }
    
    static final ClassMatcher createClassMatcher() {
        final Set<String> jdbcClasses = JDBCClassTransformer.getJdbcStatementClasses();
        return ExactClassMatcher.or((String[])jdbcClasses.toArray(new String[0]));
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object statement, final Object[] args) {
        if (statement instanceof PreparedStatementExtension) {
            final PreparedStatementExtension preparedStatement = (PreparedStatementExtension)statement;
            Object[] params = preparedStatement._nr_getSqlParameters();
            if (params != null) {
                try {
                    int index = (int)args[0];
                    --index;
                    final Object value = args[1];
                    if (index < 0) {
                        this.logger.finer("Unable to store a prepared statement parameter because the index < 0");
                        return;
                    }
                    if (index >= params.length) {
                        params = growParameterArray(params, index);
                        preparedStatement._nr_setSqlParameters(params);
                    }
                    params[index] = value;
                }
                catch (Exception e) {
                    if (this.logger.isLoggable(Level.FINE)) {
                        final String msg = MessageFormat.format("Instrumentation error for {0} in {1}: {2}", sig.toString(), ParameterizedPreparedStatementPointCut.class.getName(), e.toString());
                        if (this.logger.isLoggable(Level.FINEST)) {
                            this.logger.log(Level.FINEST, msg, e);
                        }
                        else {
                            this.logger.log(Level.FINE, msg);
                        }
                    }
                }
            }
        }
    }
    
    static Object[] growParameterArray(final Object[] params, final int missingIndex) {
        final int length = Math.max(10, (int)(missingIndex * 1.2));
        final Object[] newParams = new Object[length];
        System.arraycopy(params, 0, newParams, 0, params.length);
        return newParams;
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    static {
        METHOD_MATCHER = OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("setInt", "(II)V"), new ExactMethodMatcher("setBoolean", "(IZ)V"), new ExactMethodMatcher("setByte", "(IB)V"), new ExactMethodMatcher("setShort", "(IS)V"), new ExactMethodMatcher("setLong", "(IJ)V"), new ExactMethodMatcher("setFloat", "(IF)V"), new ExactMethodMatcher("setDouble", "(ID)V"), new ExactMethodMatcher("setBigDecimal", "(ILjava/math/BigDecimal;)V"), new ExactMethodMatcher("setString", "(ILjava/lang/String;)V"), new ExactMethodMatcher("setDate", "(ILjava/sql/Date;)V"), new ExactMethodMatcher("setTime", "(ILjava/sql/Time;)V"), new ExactMethodMatcher("setTimestamp", "(ILjava/sql/Timestamp;)V"), new ExactMethodMatcher("setNull", "(II)V"));
    }
}
