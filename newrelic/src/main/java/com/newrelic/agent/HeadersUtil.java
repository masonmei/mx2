// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import com.newrelic.api.agent.InboundHeaders;
import java.util.Set;

public class HeadersUtil
{
    public static final String NEWRELIC_ID_HEADER = "X-NewRelic-ID";
    public static final String NEWRELIC_ID_MESSAGE_HEADER = "NewRelicID";
    public static final String NEWRELIC_TRANSACTION_HEADER = "X-NewRelic-Transaction";
    public static final String NEWRELIC_TRANSACTION_MESSAGE_HEADER = "NewRelicTransaction";
    public static final String NEWRELIC_APP_DATA_HEADER = "X-NewRelic-App-Data";
    public static final String NEWRELIC_APP_DATA_MESSAGE_HEADER = "NewRelicAppData";
    public static final String NEWRELIC_SYNTHETICS_HEADER = "X-NewRelic-Synthetics";
    public static final String NEWRELIC_SYNTHETICS_MESSAGE_HEADER = "NewRelicSynthetics";
    public static final int SYNTHETICS_MIN_VERSION = 1;
    public static final int SYNTHETICS_MAX_VERSION = 1;
    public static final int SYNTHETICS_VERSION_NONE = -1;
    public static final Set<String> NEWRELIC_HEADERS;
    
    private HeadersUtil() {
        throw new UnsupportedOperationException();
    }
    
    public static String getIdHeader(final InboundHeaders headers) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-ID", "NewRelicID");
        return (key == null) ? null : headers.getHeader(key);
    }
    
    public static String getTransactionHeader(final InboundHeaders headers) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-Transaction", "NewRelicTransaction");
        return (key == null) ? null : headers.getHeader(key);
    }
    
    public static String getAppDataHeader(final InboundHeaders headers) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-App-Data", "NewRelicAppData");
        return (key == null) ? null : headers.getHeader(key);
    }
    
    public static String getSyntheticsHeader(final InboundHeaders headers) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-Synthetics", "NewRelicSynthetics");
        return (key == null) ? null : headers.getHeader(key);
    }
    
    public static void setIdHeader(final OutboundHeaders headers, final String crossProcessId) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-ID", "NewRelicID");
        headers.setHeader(key, crossProcessId);
    }
    
    public static void setTransactionHeader(final OutboundHeaders headers, final String value) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-Transaction", "NewRelicTransaction");
        headers.setHeader(key, value);
    }
    
    public static void setAppDataHeader(final OutboundHeaders headers, final String value) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-App-Data", "NewRelicAppData");
        headers.setHeader(key, value);
    }
    
    public static void setSyntheticsHeader(final OutboundHeaders headers, final String value) {
        final String key = getTypedHeaderKey(headers.getHeaderType(), "X-NewRelic-Synthetics", "NewRelicSynthetics");
        headers.setHeader(key, value);
    }
    
    private static String getTypedHeaderKey(final HeaderType type, final String httpHeader, final String messageHeader) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case MESSAGE: {
                return messageHeader;
            }
            default: {
                return httpHeader;
            }
        }
    }
    
    static {
        NEWRELIC_HEADERS = ImmutableSet.of("X-NewRelic-ID", "NewRelicID", "X-NewRelic-Transaction", "NewRelicTransaction", "X-NewRelic-App-Data", "NewRelicAppData", "X-NewRelic-Synthetics", "NewRelicSynthetics");
    }
}
