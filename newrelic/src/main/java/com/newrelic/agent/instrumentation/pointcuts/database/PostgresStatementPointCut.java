// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class PostgresStatementPointCut extends AbstractPreparedStatementPointCut
{
    static final ExactClassMatcher POSTGRESQL_STATEMENT_CLASS_MATCHER;
    
    public PostgresStatementPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(PostgresStatementPointCut.class), PostgresStatementPointCut.POSTGRESQL_STATEMENT_CLASS_MATCHER);
    }
    
    static {
        POSTGRESQL_STATEMENT_CLASS_MATCHER = new ExactClassMatcher("org/postgresql/jdbc2/AbstractJdbc2Statement");
    }
}
