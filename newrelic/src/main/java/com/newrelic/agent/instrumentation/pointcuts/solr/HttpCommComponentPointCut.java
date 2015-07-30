// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.solr;

import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class HttpCommComponentPointCut extends AbstractSolrPointCut
{
    public HttpCommComponentPointCut(final ClassTransformer classTransformer) {
        super(HttpCommComponentPointCut.class, new ExactClassMatcher("org/apache/solr/handler/component/HttpCommComponent"), PointCut
                .createExactMethodMatcher("submit", "(Lorg/apache/solr/handler/component/ShardRequest;Ljava/lang/String;Lorg/apache/solr/common/params/ModifiableSolrParams;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object component, final Object[] args) {
        return new DefaultTracer(transaction, sig, component, new ClassMethodMetricNameFormat(sig, component, "Solr"));
    }
}
