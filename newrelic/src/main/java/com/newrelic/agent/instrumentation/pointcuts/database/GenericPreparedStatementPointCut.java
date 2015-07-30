// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class GenericPreparedStatementPointCut extends AbstractPreparedStatementPointCut
{
    public static final String PREPARED_STATEMENT_INTERFACE = "java/sql/PreparedStatement";
    
    public GenericPreparedStatementPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("jdbc_prepared_statement", null, ServiceFactory.getConfigService().getDefaultAgentConfig().isGenericJDBCSupportEnabled()), new InterfaceMatcher("java/sql/PreparedStatement"));
        this.setPriority(Integer.MIN_VALUE);
    }
}
