// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.Collection;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import java.util.ArrayList;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import java.util.Set;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.TracerFactory;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class CreatePreparedStatementPointCut extends PointCut
{
    public static final String CONNECTION_INTERFACE = "java/sql/Connection";
    private static final String MYSQL_CONNECTION_CLASS = "com/mysql/jdbc/Connection";
    static final MethodMatcher METHOD_MATCHER;
    private final TracerFactory tracerFactory;
    
    public CreatePreparedStatementPointCut(final ClassTransformer classTransformer) {
        this(ServiceFactory.getConfigService().getDefaultAgentConfig());
    }
    
    private CreatePreparedStatementPointCut(final AgentConfig config) {
        super(new PointCutConfiguration("jdbc_prepare_statement", null, isEnabledByDefault()), getClassMatcher(config), CreatePreparedStatementPointCut.METHOD_MATCHER);
        this.tracerFactory = new CreatePreparedStatementTracerFactory();
    }
    
    protected static boolean isEnabledByDefault() {
        final Set<String> jdbcSupport = ServiceFactory.getConfigService().getDefaultAgentConfig().getJDBCSupport();
        return jdbcSupport.size() != 1 || !jdbcSupport.contains("mysql");
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    private static ClassMatcher getClassMatcher(final AgentConfig agentConfig) {
        final Collection<ClassMatcher> matchers = new ArrayList<ClassMatcher>(2);
        if (agentConfig.isGenericJDBCSupportEnabled()) {
            matchers.add(new InterfaceMatcher("java/sql/Connection"));
        }
        matchers.add(new ExactClassMatcher("com/mysql/jdbc/Connection"));
        matchers.add(new ExactClassMatcher("oracle/jdbc/driver/PhysicalConnection"));
        matchers.add(new ExactClassMatcher("oracle/jdbc/OracleConnectionWrapper"));
        return OrClassMatcher.getClassMatcher((ClassMatcher[])matchers.toArray(new ClassMatcher[0]));
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this.tracerFactory;
    }
    
    static {
        METHOD_MATCHER = OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("prepareStatement", new String[] { "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", "(Ljava/lang/String;III)Ljava/sql/PreparedStatement;", "(Ljava/lang/String;II)Ljava/sql/PreparedStatement;", "(Ljava/lang/String;I)Ljava/sql/PreparedStatement;", "(Ljava/lang/String;[I)Ljava/sql/PreparedStatement;", "(Ljava/lang/String;[Ljava/lang/String;)Ljava/sql/PreparedStatement;" }), new ExactMethodMatcher("prepareCall", new String[] { "(Ljava/lang/String;)Ljava/sql/CallableStatement;", "(Ljava/lang/String;II)Ljava/sql/CallableStatement;", "(Ljava/lang/String;III)Ljava/sql/CallableStatement;" }));
    }
}
