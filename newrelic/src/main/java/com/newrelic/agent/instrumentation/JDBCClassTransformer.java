// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.util.Arrays;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.config.ClassTransformerConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.NoMatchMatcher;
import com.newrelic.agent.instrumentation.pointcuts.database.PreparedStatementExtension;
import java.util.List;

public class JDBCClassTransformer extends AbstractImplementationClassTransformer
{
    public static final String DERBY_PREPARED_STATEMENT = "org/apache/derby/impl/jdbc/EmbedPreparedStatement";
    private static final List<String> DEFAULT_JDBC_STATEMENT_CLASSES;
    private boolean genericJdbcSupportEnabled;
    
    public JDBCClassTransformer(final ClassTransformer classTransformer) {
        super(classTransformer, true, PreparedStatementExtension.class, getJdbcStatementClassMatcher(), NoMatchMatcher.MATCHER, "java/sql/PreparedStatement");
        this.genericJdbcSupportEnabled = true;
    }
    
    private static ClassMatcher getJdbcStatementClassMatcher() {
        final StringBuilder sb = new StringBuilder();
        sb.append("JDBC statement classes: ");
        final Set<String> jdbcClasses = getJdbcStatementClasses();
        for (final String jdbcClass : jdbcClasses) {
            sb.append("\n").append(jdbcClass);
        }
        Agent.LOG.fine(sb.toString());
        return ExactClassMatcher.or((String[])jdbcClasses.toArray(new String[0]));
    }
    
    public static Set<String> getJdbcStatementClasses() {
        final Set<String> result = new HashSet<String>();
        result.addAll(JDBCClassTransformer.DEFAULT_JDBC_STATEMENT_CLASSES);
        result.addAll(getJdbcStatementClassesInConfig());
        return Collections.unmodifiableSet((Set<? extends String>)result);
    }
    
    private static List<String> getJdbcStatementClassesInConfig() {
        final List<String> result = new LinkedList<String>();
        final ClassTransformerConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig().getClassTransformerConfig();
        for (final String configClass : config.getJdbcStatements()) {
            result.add(configClass);
        }
        return result;
    }
    
    protected ClassVisitor createClassVisitor(final ClassReader cr, final ClassWriter cw, final String className, final ClassLoader loader) {
        ClassVisitor adapter = new AddInterfaceAdapter(cw, className, PreparedStatementExtension.class);
        adapter = RequireMethodsAdapter.getRequireMethodsAdaptor(adapter, className, PreparedStatementExtension.class, loader);
        adapter = new FieldAccessorGeneratingClassAdapter(adapter, className, PreparedStatementExtension.class);
        return adapter;
    }
    
    protected boolean isGenericInterfaceSupportEnabled() {
        return this.genericJdbcSupportEnabled;
    }
    
    static {
        DEFAULT_JDBC_STATEMENT_CLASSES = Arrays.asList("org/apache/derby/impl/jdbc/EmbedPreparedStatement", "com/mysql/jdbc/PreparedStatement", "com/microsoft/sqlserver/jdbc/SQLServerPreparedStatement", "net/sourceforge/jtds/jdbc/JtdsPreparedStatement", "oracle/jdbc/driver/OraclePreparedStatementWrapper", "org/postgresql/jdbc2/AbstractJdbc2Statement", "oracle/jdbc/driver/OraclePreparedStatement");
    }
}
