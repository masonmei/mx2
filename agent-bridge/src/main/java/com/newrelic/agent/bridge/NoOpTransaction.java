// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import com.newrelic.api.agent.InboundHeaders;
import java.util.Map;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.api.agent.TransactionNamePriority;

public class NoOpTransaction implements Transaction
{
    public static final Transaction INSTANCE;
    public static final NoOpMap<String, Object> AGENT_ATTRIBUTES;
    
    public void beforeSendResponseHeaders() {
    }
    
    public boolean setTransactionName(final TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        return false;
    }
    
    public boolean setTransactionName(final com.newrelic.agent.bridge.TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        return false;
    }
    
    public boolean isTransactionNameSet() {
        return false;
    }
    
    public TracedMethod getLastTracer() {
        return null;
    }
    
    public TracedMethod getTracedMethod() {
        return null;
    }
    
    public boolean isStarted() {
        return false;
    }
    
    public void setApplicationName(final ApplicationNamePriority priority, final String appName) {
    }
    
    public boolean isAutoAppNamingEnabled() {
        return false;
    }
    
    public boolean isWebRequestSet() {
        return false;
    }
    
    public boolean isWebResponseSet() {
        return false;
    }
    
    public void setWebRequest(final Request request) {
    }
    
    public void setWebResponse(final Response response) {
    }
    
    public WebResponse getWebResponse() {
        return NoOpWebResponse.INSTANCE;
    }
    
    public void convertToWebTransaction() {
    }
    
    public boolean isWebTransaction() {
        return false;
    }
    
    public void requestInitialized(final Request request, final Response response) {
    }
    
    public void requestDestroyed() {
    }
    
    public void ignore() {
    }
    
    public void ignoreApdex() {
    }
    
    public void saveMessageParameters(final Map<String, String> parameters) {
    }
    
    public CrossProcessState getCrossProcessState() {
        return NoOpCrossProcessState.INSTANCE;
    }
    
    public TracedMethod startFlyweightTracer() {
        return null;
    }
    
    public void finishFlyweightTracer(final TracedMethod parent, final long startInNanos, final long finishInNanos, final String className, final String methodName, final String methodDesc, final String metricName, final String[] rollupMetricNames) {
    }
    
    public Map<String, Object> getAgentAttributes() {
        return NoOpTransaction.AGENT_ATTRIBUTES;
    }
    
    public boolean registerAsyncActivity(final Object activityContext) {
        return false;
    }
    
    public boolean startAsyncActivity(final Object activityContext) {
        return false;
    }
    
    public boolean ignoreAsyncActivity(final Object activityContext) {
        return false;
    }
    
    public void provideHeaders(final InboundHeaders headers) {
    }
    
    public String getRequestMetadata() {
        return NoOpCrossProcessState.INSTANCE.getRequestMetadata();
    }
    
    public void processRequestMetadata(final String metadata) {
    }
    
    public String getResponseMetadata() {
        return NoOpCrossProcessState.INSTANCE.getResponseMetadata();
    }
    
    public void processResponseMetadata(final String metadata) {
    }
    
    static {
        INSTANCE = new NoOpTransaction();
        AGENT_ATTRIBUTES = new NoOpMap<String, Object>();
    }
    
    static final class NoOpMap<K, V> implements Map<K, V>
    {
        public int size() {
            return 0;
        }
        
        public boolean isEmpty() {
            return true;
        }
        
        public boolean containsKey(final Object key) {
            return false;
        }
        
        public boolean containsValue(final Object value) {
            return false;
        }
        
        public V get(final Object key) {
            return null;
        }
        
        public V put(final K key, final V value) {
            return null;
        }
        
        public V remove(final Object key) {
            return null;
        }
        
        public void putAll(final Map<? extends K, ? extends V> m) {
        }
        
        public void clear() {
        }
        
        public Set<K> keySet() {
            return Collections.emptySet();
        }
        
        public Collection<V> values() {
            return (Collection<V>)Collections.emptyList();
        }
        
        public Set<Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }
    }
}
