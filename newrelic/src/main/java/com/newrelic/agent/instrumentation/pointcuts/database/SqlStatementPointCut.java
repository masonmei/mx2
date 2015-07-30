// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import java.util.Collection;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import java.util.ArrayList;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SqlStatementPointCut extends TracerFactoryPointCut
{
    static final String MYSQL_STATEMENT_CLASS = "com/mysql/jdbc/Statement";
    public static final String SQL_STATEMENT_CLASS = "java/sql/Statement";
    static final String EXECUTE_METHOD_NAME = "execute";
    static final String EXECUTE_UPDATE_METHOD_NAME = "executeUpdate";
    static final String EXECUTE_QUERY_METHOD_NAME = "executeQuery";
    private static final String EXECUTE_QUERY_METHOD_DESC = "(Ljava/lang/String;)Ljava/sql/ResultSet;";
    private static final MethodMatcher METHOD_MATCHER;
    
    public SqlStatementPointCut(final ClassTransformer classTransformer) {
        this(ServiceFactory.getConfigService().getDefaultAgentConfig());
    }
    
    private SqlStatementPointCut(final AgentConfig config) {
        super(new PointCutConfiguration("jdbc_statement"), getClassMatcher(config), SqlStatementPointCut.METHOD_MATCHER);
    }
    
    private static ClassMatcher getClassMatcher(final AgentConfig agentConfig) {
        final Collection<ClassMatcher> matchers = new ArrayList<ClassMatcher>(2);
        if (agentConfig.isGenericJDBCSupportEnabled()) {
            matchers.add(new InterfaceMatcher("java/sql/Statement"));
        }
        if (agentConfig.getJDBCSupport().contains("mysql")) {
            matchers.add(new ExactClassMatcher("com/mysql/jdbc/Statement"));
        }
        return OrClassMatcher.getClassMatcher(matchers);
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object statement, final Object[] args) {
        if (args.length <= 0) {
            return null;
        }
        final Tracer parent = transaction.getTransactionActivity().getLastTracer();
        if (parent instanceof SqlStatementTracer) {
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                final String msg = MessageFormat.format("Skipping sql statement because last tracer is a SqlStatementTracer: {0}", statement.getClass().getName());
                Agent.LOG.finest(msg);
            }
            return null;
        }
        final DefaultStatementData stmtWrapper = new DefaultStatementData(transaction.getDatabaseStatementParser(), (Statement)statement, (String)args[0]);
        if (Agent.LOG.isLoggable(Level.FINEST)) {
            final String msg2 = MessageFormat.format("Created SqlStatementTracer for: {0}", statement.getClass().getName());
            Agent.LOG.finest(msg2);
        }
        return new SqlStatementTracer(transaction, sig, statement, stmtWrapper);
    }
    
    static {
        METHOD_MATCHER = OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("execute", new String[] { "(Ljava/lang/String;)Z", "(Ljava/lang/String;I)Z", "(Ljava/lang/String;[I)Z", "(Ljava/lang/String;[Ljava/lang/String;)Z" }), new ExactMethodMatcher("executeUpdate", new String[] { "(Ljava/lang/String;)I", "(Ljava/lang/String;I)I", "(Ljava/lang/String;[I)I", "(Ljava/lang/String;[Ljava/lang/String;)I" }), new ExactMethodMatcher("executeQuery", "(Ljava/lang/String;)Ljava/sql/ResultSet;"));
    }
}
