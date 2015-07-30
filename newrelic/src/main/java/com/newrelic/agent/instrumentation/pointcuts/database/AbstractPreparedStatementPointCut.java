// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.logging.IAgentLogger;
import java.util.regex.Pattern;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public abstract class AbstractPreparedStatementPointCut extends TracerFactoryPointCut
{
    private static final String PARAMETER_REGEX = "\\?";
    private static final Pattern PARAMETER_PATTERN;
    private final IAgentLogger logger;
    static final MethodMatcher METHOD_MATCHER;
    
    protected AbstractPreparedStatementPointCut(final PointCutConfiguration config, final ClassMatcher classMatcher) {
        super(config, classMatcher, AbstractPreparedStatementPointCut.METHOD_MATCHER);
        this.logger = Agent.LOG.getChildLogger(this.getClass());
    }
    
    protected IAgentLogger getLogger() {
        return this.logger;
    }
    
    public final Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object preparedStatement, final Object[] args) {
        if (preparedStatement instanceof PreparedStatementExtension) {
            final StatementData statementData = ((PreparedStatementExtension)preparedStatement)._nr_getStatementData();
            if (statementData != null) {
                if (this.logger.isLoggable(Level.FINEST)) {
                    final String msg = MessageFormat.format("Created PreparedStatementTracer for: {0}", preparedStatement.getClass().getName());
                    this.logger.finest(msg);
                }
                return new PreparedStatementTracer(transaction, sig, (PreparedStatementExtension)preparedStatement, statementData);
            }
            if (this.logger.isLoggable(Level.FINEST)) {
                try {
                    final String msg = MessageFormat.format("Statement data is null: {0},{1}", sig, ((PreparedStatement)preparedStatement).getConnection().getClass().getName());
                    this.logger.finest(msg);
                }
                catch (Throwable ex) {
                    this.logger.finest(MessageFormat.format("Statement data is null: {0}", sig));
                }
            }
        }
        else if (this.logger.isLoggable(Level.FINEST)) {
            final String msg2 = MessageFormat.format("PreparedStatement does not implement PreparedStatementExtension: {0}", preparedStatement.getClass().getName());
            this.logger.finest(msg2);
        }
        return null;
    }
    
    public static String parameterizeSql(final String sql, final Object[] parameters) throws Exception {
        if (sql == null || parameters == null || parameters.length == 0) {
            return sql;
        }
        final String[] pieces = AbstractPreparedStatementPointCut.PARAMETER_PATTERN.split(sql);
        final StringBuilder sb = new StringBuilder(sql.length() * 2);
        for (int i = 0, j = 1; i < pieces.length; ++i, ++j) {
            final String piece = pieces[i];
            if (j == pieces.length && sql.endsWith(piece)) {
                sb.append(piece);
            }
            else {
                final Object val = (i < parameters.length) ? parameters[i] : null;
                if (val instanceof Number) {
                    sb.append(piece).append(val.toString());
                }
                else if (val == null) {
                    sb.append(piece).append("?");
                }
                else {
                    sb.append(piece).append("'").append(val.toString()).append("'");
                }
            }
        }
        return sb.toString();
    }
    
    static {
        PARAMETER_PATTERN = Pattern.compile("\\?");
        METHOD_MATCHER = OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("executeQuery", "()Ljava/sql/ResultSet;"), new ExactMethodMatcher("executeUpdate", "()I"), new ExactMethodMatcher("execute", "()Z"));
    }
}
