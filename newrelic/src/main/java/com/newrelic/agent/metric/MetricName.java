// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.metric;

import java.io.IOException;
import java.util.Map;
import com.newrelic.agent.deps.org.json.simple.JSONObject;
import java.util.HashMap;
import java.io.Writer;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public final class MetricName implements JSONStreamAware
{
    public static final MetricName WEB_TRANSACTION_ORM_ALL;
    public static final MetricName OTHER_TRANSACTION_ORM_ALL;
    public static final MetricName WEB_TRANSACTION_SOLR_ALL;
    public static final MetricName OTHER_TRANSACTION_SOLR_ALL;
    public static final MetricName QUEUE_TIME;
    public static final MetricName WEBFRONTEND_WEBSERVER_ALL;
    private static final String NAME_KEY = "name";
    private static final String SCOPE_KEY = "scope";
    public static final String EMPTY_SCOPE = "";
    private final String name;
    private final String scope;
    private final int hashCode;
    
    private MetricName(final String name, final String scope) {
        this.name = name;
        this.scope = scope;
        this.hashCode = generateHashCode(name, scope);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public boolean isScoped() {
        return this.scope != "";
    }
    
    public int hashCode() {
        return this.hashCode;
    }
    
    public static int generateHashCode(final String name, final String scope) {
        final int prime = 31;
        int result = 1;
        result = 31 * result + name.hashCode();
        result = 31 * result + scope.hashCode();
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MetricName other = (MetricName)obj;
        return this.name.equals(other.name) && this.scope.equals(other.scope);
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.name);
        if (this.isScoped()) {
            sb.append(" (").append(this.scope).append(')');
        }
        return sb.toString();
    }
    
    public void writeJSONString(final Writer writer) throws IOException {
        final Map<String, Object> props = new HashMap<String, Object>(3);
        if (this.isScoped()) {
            props.put("scope", this.scope);
        }
        props.put("name", this.name);
        JSONObject.writeJSONString(props, writer);
    }
    
    public static MetricName create(final String name, String scope) {
        if (name == null || name.length() == 0) {
            return null;
        }
        if (scope == null || scope.length() == 0) {
            scope = "";
        }
        return new MetricName(name, scope);
    }
    
    public static MetricName create(final String name) {
        return create(name, null);
    }
    
    public static MetricName parseJSON(final JSONObject jsonObj) {
        final String scope = String.class.cast(jsonObj.get("scope"));
        final String name = String.class.cast(jsonObj.get("name"));
        return create(name, scope);
    }
    
    static {
        WEB_TRANSACTION_ORM_ALL = create("ORM/allWeb");
        OTHER_TRANSACTION_ORM_ALL = create("ORM/allOther");
        WEB_TRANSACTION_SOLR_ALL = create("Solr/allWeb");
        OTHER_TRANSACTION_SOLR_ALL = create("Solr/allOther");
        QUEUE_TIME = create("WebFrontend/QueueTime");
        WEBFRONTEND_WEBSERVER_ALL = create("WebFrontend/WebServer/all");
    }
}
