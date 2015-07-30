// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class MySQLPreparedStatementPointCut extends AbstractPreparedStatementPointCut
{
    public static final String MYSQL_PREPARED_STATEMENT_CLASS_NAME = "com/mysql/jdbc/PreparedStatement";
    
    public MySQLPreparedStatementPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("jdbc_mysql_prepared_statement", null, isEnabledByDefault()), new ExactClassMatcher("com/mysql/jdbc/PreparedStatement"));
    }
    
    protected static boolean isEnabledByDefault() {
        final AgentConfig agentConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
        final boolean genericJdbcEnabled = agentConfig.isGenericJDBCSupportEnabled();
        return genericJdbcEnabled || agentConfig.getJDBCSupport().contains("mysql");
    }
}
