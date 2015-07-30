// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.solr;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.dispatchers.Dispatcher;
import java.util.Map;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SolrRequestHandlerPointCut extends AbstractSolrPointCut
{
    private static SolrReflectionHelper sSolrReflectionHelper;
    private static final String SOLR = "Solr";
    
    public SolrRequestHandlerPointCut(final ClassTransformer classTransformer) {
        super(SolrRequestHandlerPointCut.class, OrClassMatcher.getClassMatcher(new ExactClassMatcher("org/apache/solr/handler/RequestHandlerBase"), new InterfaceMatcher("org/apache/solr/request/SolrRequestHandler")), PointCut.createExactMethodMatcher("handleRequest", "(Lorg/apache/solr/request/SolrQueryRequest;Lorg/apache/solr/request/SolrQueryResponse;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object handler, final Object[] args) {
        final String type = this.getQueryType(handler, args[0]);
        return new DefaultTracer(transaction, sig, handler, new ClassMethodMetricNameFormat(sig, handler, "SolrRequestHandler")) {
            protected void doFinish(final int opcode, final Object returnValue) {
                super.doFinish(opcode, returnValue);
                final Dispatcher dispatcher = transaction.getDispatcher();
                final Object rb = transaction.getTransactionCache().removeSolrResponseBuilderParamName();
                if (rb != null) {
                    final Map<String, String> result = SolrRequestHandlerPointCut.addDebugInfo(rb);
                    transaction.getAgentAttributes().putAll(result);
                }
                if (dispatcher.isWebTransaction()) {
                    final String uri = dispatcher.getUri();
                    final StringBuilder builder = new StringBuilder();
                    builder.append(uri);
                    if (type != null) {
                        if (!uri.endsWith("/")) {
                            builder.append('/');
                        }
                        builder.append(type);
                    }
                    this.setTransactionName(transaction, builder.toString());
                }
            }
            
            private void setTransactionName(final Transaction tx, String uri) {
                if (!tx.isTransactionNamingEnabled()) {
                    return;
                }
                final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
                if (policy.canSetTransactionName(tx, TransactionNamePriority.FRAMEWORK)) {
                    uri = ServiceFactory.getNormalizationService().getUrlNormalizer(tx.getApplicationName()).normalize(uri);
                    if (uri == null) {
                        tx.setIgnore(true);
                    }
                    else {
                        if (Agent.LOG.isLoggable(Level.FINER)) {
                            final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Solr request URI", uri);
                            Agent.LOG.finer(msg);
                        }
                        policy.setTransactionName(transaction, uri, "Solr", TransactionNamePriority.FRAMEWORK);
                    }
                }
            }
        };
    }
    
    static SolrReflectionHelper getSolrReflectionHelper(final Object rb) throws Exception {
        if (SolrRequestHandlerPointCut.sSolrReflectionHelper == null) {
            SolrRequestHandlerPointCut.sSolrReflectionHelper = new SolrReflectionHelper(rb);
        }
        return SolrRequestHandlerPointCut.sSolrReflectionHelper;
    }
    
    static Map<String, String> addDebugInfo(final Object rb) {
        final Map<String, String> result = new HashMap<String, String>();
        try {
            final SolrReflectionHelper helper = getSolrReflectionHelper(rb);
            final Object solrQueryRequest = helper.reqField.get(rb);
            final Object solrParams = helper.getParamsMethod.invoke(solrQueryRequest, new Object[0]);
            final String rawQueryString = (String)solrParams.getClass().getMethod("get", String.class).invoke(solrParams, "q");
            result.put("library.solr.raw_query_string", rawQueryString);
            final String queryString = (String)helper.getQueryString.invoke(rb, new Object[0]);
            result.put("library.solr.query_string", queryString);
            final Object schema = helper.getSchemaMethod.invoke(solrQueryRequest, new Object[0]);
            final Object query = helper.getQuery.invoke(rb, new Object[0]);
            final String parsedQuery = (String)helper.queryParsingClass.getMethod("toString", helper.queryClass, helper.indexSchemaClass).invoke(null, query, schema);
            result.put("library.solr.lucene_query", parsedQuery);
            final String parsedQueryToString = query.toString();
            result.put("library.solr.lucene_query_string", parsedQueryToString);
        }
        catch (Throwable e) {
            result.put("library.solr.solr_debug_info_error", e.toString());
            final String msg = MessageFormat.format("Error in Solr debug data collection - {0}", e.toString());
            Agent.LOG.finer(msg);
            Agent.LOG.log(Level.FINEST, msg, e);
        }
        return result;
    }
    
    private String getQueryType(final Object handler, final Object request) {
        try {
            final Object queryType = request.getClass().getClassLoader().loadClass("org.apache.solr.request.SolrQueryRequest").getMethod("getQueryType", (Class<?>[])new Class[0]).invoke(request, new Object[0]);
            if (queryType != null) {
                return queryType.toString();
            }
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unable to get the SolrQueryRequest query type : {0}", e.toString());
            Agent.LOG.info(msg);
            Agent.LOG.log(Level.FINER, msg, e);
        }
        return null;
    }
    
    static {
        SolrRequestHandlerPointCut.sSolrReflectionHelper = null;
    }
    
    private static class SolrReflectionHelper
    {
        public final Class<?> solrPluginUtilsClass;
        public final Class<?> solrQueryRequestClass;
        public final Class<?> docListClass;
        public final Class<?> queryClass;
        public final Class<?> queryParsingClass;
        public final Class<?> indexSchemaClass;
        public final Field reqField;
        public final Method getQueryString;
        public final Method getQuery;
        public final Method getResults;
        public final Method getParamsMethod;
        public final Method getSchemaMethod;
        
        public SolrReflectionHelper(final Object rb) throws Exception {
            final Class<?> responseBuilderClass = rb.getClass();
            final ClassLoader cl = responseBuilderClass.getClassLoader();
            this.solrPluginUtilsClass = cl.loadClass("org.apache.solr.util.SolrPluginUtils");
            this.solrQueryRequestClass = cl.loadClass("org.apache.solr.request.SolrQueryRequest");
            this.docListClass = cl.loadClass("org.apache.solr.search.DocList");
            this.queryClass = cl.loadClass("org.apache.lucene.search.Query");
            this.queryParsingClass = cl.loadClass("org.apache.solr.search.QueryParsing");
            this.indexSchemaClass = cl.loadClass("org.apache.solr.schema.IndexSchema");
            this.reqField = responseBuilderClass.getDeclaredField("req");
            this.getQueryString = responseBuilderClass.getMethod("getQueryString", (Class<?>[])new Class[0]);
            this.getQuery = responseBuilderClass.getMethod("getQuery", (Class<?>[])new Class[0]);
            this.getResults = responseBuilderClass.getMethod("getResults", (Class<?>[])new Class[0]);
            this.getParamsMethod = this.solrQueryRequestClass.getMethod("getParams", (Class<?>[])new Class[0]);
            this.getSchemaMethod = this.solrQueryRequestClass.getMethod("getSchema", (Class<?>[])new Class[0]);
        }
    }
}
