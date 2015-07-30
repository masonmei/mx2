// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.solr;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SearchComponentPointCut extends AbstractSolrPointCut
{
    public SearchComponentPointCut(final ClassTransformer classTransformer) {
        super(SearchComponentPointCut.class, new ChildClassMatcher("org/apache/solr/handler/component/SearchComponent"), OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("handleResponses", "(Lorg/apache/solr/handler/component/ResponseBuilder;Lorg/apache/solr/handler/component/ShardRequest;)V"), new ExactMethodMatcher("prepare", "(Lorg/apache/solr/handler/component/ResponseBuilder;)V"), new ExactMethodMatcher("process", "(Lorg/apache/solr/handler/component/ResponseBuilder;)V")));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object component, final Object[] args) {
        transaction.getTransactionCache().putSolrResponseBuilderParamName(args[0]);
        return new DefaultTracer(transaction, sig, component, new ClassMethodMetricNameFormat(sig, component, "Solr"));
    }
}
