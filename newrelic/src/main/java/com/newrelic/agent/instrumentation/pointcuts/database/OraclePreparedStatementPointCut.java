// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class OraclePreparedStatementPointCut extends AbstractPreparedStatementPointCut
{
    private static final String NAME = "jdbc_oracle_prepared_statement";
    public static final String ORACLE_PREPARED_STATEMENT_CLASS_NAME_MATCH = "oracle/jdbc/driver/OraclePreparedStatement";
    private static final String ORACLE_CALLABLE_STATEMENT_CLASS_NAME_MATCH = "oracle/jdbc/driver/OracleCallableStatement";
    public static final String ORACLE_PREPARED_STATEMENT_CLASS_NAME = "oracle.jdbc.driver.OraclePreparedStatement";
    public static final String ORACLE_CALLABLE_STATEMENT_CLASS_NAME = "oracle.jdbc.driver.OracleCallableStatement";
    
    public OraclePreparedStatementPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("jdbc_oracle_prepared_statement", null, true), ExactClassMatcher.or("oracle/jdbc/driver/OraclePreparedStatement", "oracle/jdbc/driver/OracleCallableStatement"));
    }
}
