// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic;

import com.newrelic.agent.deps.org.slf4j.MarkerFactory;
import com.newrelic.agent.deps.org.slf4j.Marker;

public class ClassicConstants
{
    public static final String USER_MDC_KEY = "user";
    public static final String LOGBACK_CONTEXT_SELECTOR = "logback.ContextSelector";
    public static final String JNDI_CONFIGURATION_RESOURCE = "java:comp/env/logback/configuration-resource";
    public static final String JNDI_CONTEXT_NAME = "java:comp/env/logback/context-name";
    public static final int MAX_DOTS = 16;
    public static final int DEFAULT_MAX_CALLEDER_DATA_DEPTH = 8;
    public static final String REQUEST_REMOTE_HOST_MDC_KEY = "req.remoteHost";
    public static final String REQUEST_USER_AGENT_MDC_KEY = "req.userAgent";
    public static final String REQUEST_REQUEST_URI = "req.requestURI";
    public static final String REQUEST_QUERY_STRING = "req.queryString";
    public static final String REQUEST_REQUEST_URL = "req.requestURL";
    public static final String REQUEST_X_FORWARDED_FOR = "req.xForwardedFor";
    public static final String GAFFER_CONFIGURATOR_FQCN = "com.newrelic.agent.deps.ch.qos.logback.classic.gaffer.GafferConfigurator";
    public static final Marker FINALIZE_SESSION_MARKER;
    
    static {
        FINALIZE_SESSION_MARKER = MarkerFactory.getMarker("FINALIZE_SESSION");
    }
}
