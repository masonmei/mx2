// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.sql.Connection;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ConnectionPointCut extends TracerFactoryPointCut
{
    public ConnectionPointCut(final ClassTransformer classTransformer) {
        super(ConnectionPointCut.class, ExactClassMatcher.or("com/newrelic/agent/deps/org/apache/commons/dbcp/PoolingDataSource$PoolGuardConnectionWrapper", "com/newrelic/agent/deps/org/apache/commons/dbcp/PoolableConnection", "org/apache/tomcat/dbcp/dbcp/cpdsadapter/ConnectionImpl", "org/jboss/resource/adapter/jdbc/WrappedConnection", "org/postgresql/jdbc2/AbstractJdbc2Connection", "weblogic/jdbc/pool/Connection", "org/postgresql/jdbc3/Jdbc3Connection", "com/mysql/jdbc/JDBC4Connection"), PointCut.createExactMethodMatcher("close", "()V"));
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object connection, final Object[] args) {
        transaction.getConnectionCache().removeConnectionFactory((Connection)connection);
        return null;
    }
}
