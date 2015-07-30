// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.solr;

import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;
import com.newrelic.agent.metric.MetricName;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.TransactionStats;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.servlet.ServletUtils;
import java.util.Iterator;
import java.util.HashMap;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.IgnoreChildSocketCalls;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SolrServerPointCut extends AbstractSolrPointCut
{
    private static final String QUERY_METHOD_NAME = "query";
    private static final String NO_ARG_DESCRIPTION = "()Lorg/apache/solr/client/solrj/response/UpdateResponse;";
    
    public SolrServerPointCut(final ClassTransformer classTransformer) {
        super(SolrServerPointCut.class, new ExactClassMatcher("org/apache/solr/client/solrj/SolrServer"), OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("add", new String[] { "(Ljava/util/Collection;)Lorg/apache/solr/client/solrj/response/UpdateResponse;", "(Lorg/apache/solr/common/SolrInputDocument;)Lorg/apache/solr/client/solrj/response/UpdateResponse;" }), new ExactMethodMatcher("commit", new String[] { "()Lorg/apache/solr/client/solrj/response/UpdateResponse;", "(ZZ)Lorg/apache/solr/client/solrj/response/UpdateResponse;" }), new ExactMethodMatcher("optimize", new String[] { "()Lorg/apache/solr/client/solrj/response/UpdateResponse;", "(ZZ)Lorg/apache/solr/client/solrj/response/UpdateResponse;", "(ZZI)Lorg/apache/solr/client/solrj/response/UpdateResponse;" }), new ExactMethodMatcher("rollback", "()Lorg/apache/solr/client/solrj/response/UpdateResponse;"), new ExactMethodMatcher("deleteById", new String[] { "(Ljava/lang/String;)Lorg/apache/solr/client/solrj/response/UpdateResponse;", "(Ljava/util/List;)Lorg/apache/solr/client/solrj/response/UpdateResponse;" }), new ExactMethodMatcher("deleteByQuery", "(Ljava/lang/String;)Lorg/apache/solr/client/solrj/response/UpdateResponse;"), new ExactMethodMatcher("ping", "()Lorg/apache/solr/client/solrj/response/SolrPingResponse;"), new ExactMethodMatcher("query", new String[] { "(Lorg/apache/solr/common/params/SolrParams;)Lorg/apache/solr/client/solrj/response/QueryResponse;", "(Lorg/apache/solr/common/params/SolrParams;Lorg/apache/solr/client/solrj/SolrRequest$METHOD;)Lorg/apache/solr/client/solrj/response/QueryResponse;" })));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object server, final Object[] args) {
        return new SolrServerTracer(transaction, sig, server, args);
    }
    
    private class SolrServerTracer extends DefaultTracer implements IgnoreChildSocketCalls
    {
        public SolrServerTracer(final Transaction transaction, final ClassMethodSignature sig, final Object server, final Object[] args) {
            super(transaction, sig, server, new ClassMethodMetricNameFormat(sig, server, "SolrClient"));
            if ("query".equals(sig.getMethodName())) {
                final Object solrParams = args[0];
                try {
                    final Map<String, String[]> paramMap = new HashMap<String, String[]>();
                    if (solrParams instanceof SolrParams) {
                        final Iterator<String> paramNamesIter = ((SolrParams)solrParams).getParameterNamesIterator();
                        while (paramNamesIter.hasNext()) {
                            final String paramName = paramNamesIter.next();
                            paramMap.put(paramName, ((SolrParams)solrParams).getParams(paramName));
                        }
                    }
                    else {
                        final Class paramsClass = solrParams.getClass();
                        final Method paramNamesIter2 = paramsClass.getMethod("getParameterNamesIterator", (Class[])new Class[0]);
                        final Method getParams = paramsClass.getMethod("getParams", String.class);
                        final Iterator<String> iter = (Iterator<String>)paramNamesIter2.invoke(solrParams, new Object[0]);
                        while (iter.hasNext()) {
                            final String paramName = iter.next();
                            final String[] values = (String[])getParams.invoke(solrParams, paramName);
                            paramMap.put(paramName, values);
                        }
                    }
                    if (!paramMap.isEmpty()) {
                        this.setAttribute("query_params", ServletUtils.getSimpleParameterMap(paramMap, transaction.getAgentConfig().getMaxUserParameterSize()));
                    }
                }
                catch (Exception e) {
                    Agent.LOG.log(Level.FINER, "Solr client error", e);
                }
            }
        }
        
        protected void doRecordMetrics(final TransactionStats transactionStats) {
            transactionStats.getUnscopedStats().getResponseTimeStats("Solr/all").recordResponseTime(this.getExclusiveDuration(), TimeUnit.NANOSECONDS);
            transactionStats.getUnscopedStats().getResponseTimeStats((this.getTransaction().isWebTransaction() ? MetricName.WEB_TRANSACTION_SOLR_ALL : MetricName.OTHER_TRANSACTION_SOLR_ALL).getName()).recordResponseTime(this.getExclusiveDuration(), TimeUnit.NANOSECONDS);
        }
    }
    
    @InterfaceMixin(originalClassName = { "org/apache/solr/common/params/SolrParams" })
    public interface SolrParams
    {
        String[] getParams(String p0);
        
        Iterator<String> getParameterNamesIterator();
    }
}
