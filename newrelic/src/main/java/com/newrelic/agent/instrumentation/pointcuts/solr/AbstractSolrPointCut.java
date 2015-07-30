// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.solr;

import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

abstract class AbstractSolrPointCut extends TracerFactoryPointCut
{
    static final String SOLR_CONFIG_GROUP_NAME = "solr";
    
    protected AbstractSolrPointCut(final Class<? extends AbstractSolrPointCut> pointCutClass, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(new PointCutConfiguration(pointCutClass.getName(), "solr", true), classMatcher, methodMatcher);
    }
    
    protected AbstractSolrPointCut(final PointCutConfiguration config, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(config, classMatcher, methodMatcher);
    }
}
