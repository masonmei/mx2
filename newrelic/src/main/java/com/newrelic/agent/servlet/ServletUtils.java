// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.servlet;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.Transaction;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import com.newrelic.agent.util.StackTraces;

public class ServletUtils
{
    private static String SERVLET_EXCEPTION_CLASS_NAME;
    public static final String NEWRELIC_IGNORE_ATTRIBUTE_NAME = "com.newrelic.agent.IGNORE";
    public static final String NEWRELIC_IGNORE_APDEX_ATTRIBUTE_NAME = "com.newrelic.agent.IGNORE_APDEX";
    public static final String APPLICATION_NAME_PARAM = "com.newrelic.agent.APPLICATION_NAME";
    public static final String TRANSACTION_NAME_PARAM = "com.newrelic.agent.TRANSACTION_NAME";
    
    public static Throwable getReportError(final Throwable throwable) {
        if (throwable != null && ServletUtils.SERVLET_EXCEPTION_CLASS_NAME.equals(throwable.getClass().getName())) {
            return StackTraces.getRootCause(throwable);
        }
        return throwable;
    }
    
    public static Map<String, String> getSimpleParameterMap(final Map<String, String[]> parameterMap, final int maxSizeLimit) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String> parameters = new HashMap<String, String>();
        for (final Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            final String name = entry.getKey();
            final String[] values = entry.getValue();
            final String value = getValue(values, maxSizeLimit);
            if (value != null) {
                parameters.put(name, value);
            }
        }
        return parameters;
    }
    
    public static void recordParameters(final Transaction tx, final Request request) {
        if (tx.isIgnore()) {
            return;
        }
        if (!ServiceFactory.getAttributesService().captureRequestParams(tx.getApplicationName())) {
            return;
        }
        final Map<String, String> requestParameters = getRequestParameterMap(request, tx.getAgentConfig().getMaxUserParameterSize());
        if (requestParameters.isEmpty()) {
            return;
        }
        tx.getPrefixedAgentAttributes().put("request.parameters.", requestParameters);
    }
    
    static Map<String, String> getRequestParameterMap(final Request request, final int maxSizeLimit) {
        final Enumeration<?> nameEnumeration = (Enumeration<?>)request.getParameterNames();
        if (nameEnumeration == null || !nameEnumeration.hasMoreElements()) {
            return Collections.emptyMap();
        }
        final Map<String, String> requestParameters = new HashMap<String, String>();
        while (nameEnumeration.hasMoreElements()) {
            final String name = nameEnumeration.nextElement().toString();
            if (name.length() > maxSizeLimit) {
                Agent.LOG.log(Level.FINER, "Rejecting request parameter with key \"{0}\" because the key is over the size limit of {1}", new Object[] { name, maxSizeLimit });
            }
            else {
                final String[] values = request.getParameterValues(name);
                final String value = getValue(values, maxSizeLimit);
                if (value == null) {
                    continue;
                }
                requestParameters.put(name, value);
            }
        }
        return requestParameters;
    }
    
    private static String getValue(final String[] values, final int maxSizeLimit) {
        if (values == null || values.length == 0) {
            return null;
        }
        String value = (values.length == 1) ? values[0] : Arrays.asList(values).toString();
        if (value != null && value.length() > maxSizeLimit) {
            if (values.length == 1) {
                value = value.substring(0, maxSizeLimit);
            }
            else {
                value = value.substring(0, maxSizeLimit - 1) + ']';
            }
        }
        return value;
    }
    
    static {
        ServletUtils.SERVLET_EXCEPTION_CLASS_NAME = "javax.servlet.ServletException";
    }
}
