// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import com.newrelic.agent.tracers.MetricNameFormatWithHost;
import java.net.URL;
import com.newrelic.agent.deps.com.google.common.cache.Cache;

public class TransactionCache
{
    private Cache<Object, URL> urlCache;
    private Cache<Object, MetricNameFormatWithHost> inputStreamCache;
    private Object solrResponseBuilder;
    
    public MetricNameFormatWithHost getMetricNameFormatWithHost(final Object key) {
        return this.getInputStreamCache().getIfPresent(key);
    }
    
    public void putMetricNameFormatWithHost(final Object key, final MetricNameFormatWithHost val) {
        this.getInputStreamCache().put(key, val);
    }
    
    private Cache<Object, MetricNameFormatWithHost> getInputStreamCache() {
        if (this.inputStreamCache == null) {
            this.inputStreamCache = CacheBuilder.newBuilder().weakKeys().build();
        }
        return this.inputStreamCache;
    }
    
    public Object removeSolrResponseBuilderParamName() {
        final Object toReturn = this.solrResponseBuilder;
        this.solrResponseBuilder = null;
        return toReturn;
    }
    
    public void putSolrResponseBuilderParamName(final Object val) {
        this.solrResponseBuilder = val;
    }
    
    public URL getURL(final Object key) {
        return this.getUrlCache().getIfPresent(key);
    }
    
    public void putURL(final Object key, final URL val) {
        this.getUrlCache().put(key, val);
    }
    
    private Cache<Object, URL> getUrlCache() {
        if (this.urlCache == null) {
            this.urlCache = CacheBuilder.newBuilder().weakKeys().build();
        }
        return this.urlCache;
    }
}
